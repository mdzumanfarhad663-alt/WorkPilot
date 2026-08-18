package com.example.data.model

enum class TaskType(val displayName: String, val description: String, val example: String) {
    MONEY("Money Task", "Direct revenue, client deliverables, billable work", "Complete and test the client checkout page"),
    GROWTH("Growth Task", "Marketing, outreach, pitching, skill expansion", "Send five customized client proposals"),
    MAINTENANCE("Maintenance Task", "Admin, email replies, invoicing, quick fixes", "Reply to all client emails and send invoice")
}
