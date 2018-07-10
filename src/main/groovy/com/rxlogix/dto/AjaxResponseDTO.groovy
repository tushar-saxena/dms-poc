package com.rxlogix.dto

import grails.converters.JSON

class AjaxResponseDTO<T> extends ResponseDTO<T>{

    int httpCode = 200
    String stackTrace = ''

    void setFailureResponse(String message, int _httpCode = 500) {
        super.setFailureResponse(message)
        this.httpCode = _httpCode
    }

    void setFailureResponse(Exception ex, String message = null, int _httpCode = 500) {
        this.message = message ?: ex.message
        this.status = false
        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));
        this.stackTrace = errors.toString()
        this.httpCode = _httpCode
    }

    def toAjaxResponse() {
        [status: httpCode, contentType: "application/json", encoding: "UTF-8", text: this as JSON]
    }
}

