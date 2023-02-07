package com.lighthouse.common.recognizer.recognizer.smilecon

import com.lighthouse.common.recognizer.recognizer.TemplateRecognizer

class SmileConRecognizer : TemplateRecognizer() {

    override val parser = SmileConParser()

    override val processor = SmileConProcessor()
}
