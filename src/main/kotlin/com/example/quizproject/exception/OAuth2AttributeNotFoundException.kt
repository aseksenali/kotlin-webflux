package com.example.quizproject.exception

data class OAuth2AttributeNotFoundException(val attributeName: String): Exception("Attribute with name $attributeName not found in token")