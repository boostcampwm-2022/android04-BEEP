package com.lighthouse.util.recognizer.recognizer.giftishow

import com.lighthouse.util.recognizer.recognizer.TemplateRecognizer

class GiftishowRecognizer : TemplateRecognizer() {

    override val parser = GiftishowParser()

    override val processor = GiftishowProcessor()
}
