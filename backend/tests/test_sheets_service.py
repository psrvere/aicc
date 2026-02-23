from unittest.mock import MagicMock, patch

import pytest

CONTACT_HEADERS = [
    "id", "name", "phone", "business", "city", "industry",
    "deal_stage", "last_called", "call_count", "last_call_summary",
    "recording_link", "next_follow_up", "notes", "created_at",
]

CALL_LOG_HEADERS = [
    "id", "contact_id", "timestamp", "duration_seconds",
    "disposition", "summary", "deal_stage", "recording_url", "transcript",
]


def _make_contact_record(**overrides):
    record = {
        "id": "uuid-1", "name": "Alice", "phone": "123",
        "business": "", "city": "", "industry": "",
        "deal_stage": "New", "last_called": "", "call_count": 0,
        "last_call_summary": "", "recording_link": "",
        "next_follow_up": "", "notes": "", "created_at": "2026-01-01T00:00:00",
    }
    record.update(overrides)
    return record


def _make_call_log_record(**overrides):
    record = {
        "id": "log-1", "contact_id": "uuid-1",
        "timestamp": "2026-02-23T10:00:00", "duration_seconds": 120,
        "disposition": "Connected", "summary": "", "deal_stage": "New",
        "recording_url": "", "transcript": "",
    }
    record.update(overrides)
    return record


@pytest.fixture
def sheets_service():
    with patch("app.services.sheets.gspread") as mock_gspread, \
         patch("app.services.sheets.settings") as mock_settings:
        mock_settings.google_service_account_json = '{"type": "service_account"}'
        mock_settings.spreadsheet_id = "test-sheet-id"

        mock_client = MagicMock()
        mock_gspread.service_account_from_dict.return_value = mock_client
        mock_spreadsheet = MagicMock()
        mock_client.open_by_key.return_value = mock_spreadsheet

        contacts_ws = MagicMock()
        call_logs_ws = MagicMock()
        mock_spreadsheet.worksheet.side_effect = lambda name: {
            "Contacts": contacts_ws,
            "CallLogs": call_logs_ws,
        }[name]

        from app.services.sheets import SheetsService
        service = SheetsService()

        yield service, contacts_ws, call_logs_ws


def test_get_all_contacts(sheets_service):
    service, contacts_ws, _ = sheets_service
    contacts_ws.get_all_records.return_value = [
        _make_contact_record(call_count=5, business="Acme"),
    ]
    result = service.get_all_contacts()
    assert len(result) == 1
    assert result[0]["name"] == "Alice"
    assert result[0]["call_count"] == 5
    assert result[0]["business"] == "Acme"
    assert result[0]["last_called"] is None  # empty string → None


def test_get_all_contacts_empty(sheets_service):
    service, contacts_ws, _ = sheets_service
    contacts_ws.get_all_records.return_value = []
    result = service.get_all_contacts()
    assert result == []


def test_get_contact_by_id_found(sheets_service):
    service, contacts_ws, _ = sheets_service
    contacts_ws.get_all_records.return_value = [
        _make_contact_record(id="uuid-1"),
        _make_contact_record(id="uuid-2", name="Bob"),
    ]
    result = service.get_contact_by_id("uuid-2")
    assert result is not None
    assert result["name"] == "Bob"


def test_get_contact_by_id_not_found(sheets_service):
    service, contacts_ws, _ = sheets_service
    contacts_ws.get_all_records.return_value = [
        _make_contact_record(id="uuid-1"),
    ]
    result = service.get_contact_by_id("nonexistent")
    assert result is None


def test_create_contact(sheets_service):
    service, contacts_ws, _ = sheets_service
    data = {"name": "Carol", "phone": "789", "business": "BigCo"}
    result = service.create_contact(data)

    contacts_ws.append_row.assert_called_once()
    row = contacts_ws.append_row.call_args[0][0]
    assert row[1] == "Carol"  # name
    assert row[2] == "789"    # phone
    assert row[3] == "BigCo"  # business
    assert row[8] == 0        # call_count default
    assert result["id"] is not None  # UUID generated
    assert result["name"] == "Carol"
    assert result["created_at"] is not None


def test_create_contact_sets_defaults(sheets_service):
    service, contacts_ws, _ = sheets_service
    data = {"name": "Dan", "phone": "000"}
    result = service.create_contact(data)

    row = contacts_ws.append_row.call_args[0][0]
    assert row[6] == "New"    # deal_stage default
    assert row[8] == 0        # call_count default
    assert result["deal_stage"] == "New"
    assert result["call_count"] == 0


def test_update_contact(sheets_service):
    service, contacts_ws, _ = sheets_service
    contacts_ws.get_all_values.return_value = [
        CONTACT_HEADERS,
        ["uuid-1", "Alice", "123", "", "", "", "New", "", "0", "", "", "", "", "2026-01-01"],
    ]
    contacts_ws.get_all_records.return_value = [
        _make_contact_record(id="uuid-1", name="Alice Updated"),
    ]
    result = service.update_contact("uuid-1", {"name": "Alice Updated"})
    contacts_ws.update_cell.assert_called_once_with(2, 2, "Alice Updated")
    assert result["name"] == "Alice Updated"


def test_update_contact_not_found(sheets_service):
    service, contacts_ws, _ = sheets_service
    contacts_ws.get_all_values.return_value = [CONTACT_HEADERS]
    with pytest.raises(ValueError, match="not found"):
        service.update_contact("nonexistent", {"name": "X"})


def test_delete_contact(sheets_service):
    service, contacts_ws, _ = sheets_service
    contacts_ws.get_all_values.return_value = [
        CONTACT_HEADERS,
        ["uuid-1", "Alice", "123", "", "", "", "New", "", "0", "", "", "", "", "2026-01-01"],
    ]
    service.delete_contact("uuid-1")
    contacts_ws.delete_rows.assert_called_once_with(2)


def test_delete_contact_not_found(sheets_service):
    service, contacts_ws, _ = sheets_service
    contacts_ws.get_all_values.return_value = [CONTACT_HEADERS]
    with pytest.raises(ValueError, match="not found"):
        service.delete_contact("nonexistent")


def test_append_call_log(sheets_service):
    service, _, call_logs_ws = sheets_service
    data = {
        "contact_id": "uuid-1",
        "duration_seconds": 180,
        "disposition": "Connected",
        "summary": "Good call",
        "deal_stage": "Qualified",
    }
    result = service.append_call_log(data)

    call_logs_ws.append_row.assert_called_once()
    row = call_logs_ws.append_row.call_args[0][0]
    assert row[1] == "uuid-1"       # contact_id
    assert row[3] == 180             # duration_seconds
    assert row[4] == "Connected"     # disposition
    assert result["id"] is not None  # UUID generated


def test_get_call_logs_for_contact(sheets_service):
    service, _, call_logs_ws = sheets_service
    call_logs_ws.get_all_records.return_value = [
        _make_call_log_record(contact_id="uuid-1"),
        _make_call_log_record(id="log-2", contact_id="uuid-2"),
        _make_call_log_record(id="log-3", contact_id="uuid-1"),
    ]
    result = service.get_call_logs_for_contact("uuid-1")
    assert len(result) == 2
    assert all(r["contact_id"] == "uuid-1" for r in result)


def test_get_call_logs_by_date(sheets_service):
    service, _, call_logs_ws = sheets_service
    call_logs_ws.get_all_records.return_value = [
        _make_call_log_record(timestamp="2026-02-23T10:00:00"),
        _make_call_log_record(id="log-2", timestamp="2026-02-22T15:00:00"),
        _make_call_log_record(id="log-3", timestamp="2026-02-23T14:30:00"),
    ]
    result = service.get_call_logs_by_date("2026-02-23")
    assert len(result) == 2
