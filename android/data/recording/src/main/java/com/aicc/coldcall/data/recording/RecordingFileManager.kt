package com.aicc.coldcall.data.recording

import java.io.File

class RecordingFileManager(private val filesDir: File) {

    private val recordingsDir: File
        get() = File(filesDir, "recordings").also { it.mkdirs() }

    fun createRecordingFile(contactId: String, timestamp: Long): File {
        val sanitizedId = contactId.replace(Regex("[^a-zA-Z0-9_-]"), "_")
        val fileName = "call_${sanitizedId}_$timestamp.m4a"
        val file = File(recordingsDir, fileName)
        if (!file.createNewFile()) {
            throw IllegalStateException(
                "Recording file already exists: $fileName (contactId=$contactId, timestamp=$timestamp)"
            )
        }
        return file
    }

    fun deleteFilesOlderThan(thresholdMs: Long): Int {
        val dir = recordingsDir
        if (!dir.exists()) return 0
        val oldFiles = dir.listFiles()?.filter { it.lastModified() < thresholdMs } ?: return 0
        return oldFiles.count { it.delete() }
    }
}
