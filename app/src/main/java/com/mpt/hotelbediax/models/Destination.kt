package com.mpt.hotelbediax.models

import java.util.Date

data class Destination(
    var id: Int,
    var name: String,
    var description: String,
    var countryCode: String,
    var type: String,
    var lastModify: String
)