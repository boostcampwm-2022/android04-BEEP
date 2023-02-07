package com.lighthouse.common.recognizer.recognizer.giftishow

import com.lighthouse.common.recognizer.recognizer.TemplateRecognizer


class GiftishowRecognizer : TemplateRecognizer() {

    override val parser = GiftishowParser()

    override val processor = GiftishowProcessor()
}
