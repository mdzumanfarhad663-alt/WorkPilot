package com.example.data.model

enum class TaskType(val slotNumber: Int, val displayName: String, val placeholder: String) {
    TASK_1(1, "Task 1", "e.g. Complete and test client checkout page"),
    TASK_2(2, "Task 2", "e.g. Send five customized client proposals"),
    TASK_3(3, "Task 3", "e.g. Reply to client emails and send invoice");

    companion object {
        fun fromString(value: String): TaskType {
            return when (value.uppercase()) {
                "TASK_1", "MONEY", "1" -> TASK_1
                "TASK_2", "GROWTH", "2" -> TASK_2
                "TASK_3", "MAINTENANCE", "3" -> TASK_3
                else -> TASK_1
            }
        }
    }
}
