package com.lighthouse.utils.recognizer.recognizer.giftishow

import com.lighthouse.utils.recognizer.recognizer.TemplateRecognizer

internal class GiftishowRecognizer : TemplateRecognizer() {

    override val parser = GiftishowParser()

    override val processor = GiftishowProcessor()
}
