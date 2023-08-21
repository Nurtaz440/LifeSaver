package com.example.ambulance.model

data class TextMessages(

    var id: String?, // Unique identifier for each comment
    val author: String, // Name or username of the author
    val content: String, // The comment text
    val timestamp: Long // Timestamp to order comments chronologically
){
    constructor() : this("","","",0L)
}
