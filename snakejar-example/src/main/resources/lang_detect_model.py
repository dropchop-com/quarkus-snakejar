import os
import fasttext

class LanguageDetectModel:

    fasttext_model = None

    @staticmethod
    def get_model():
        if not LanguageDetectModel.fasttext_model:
            # print("Loading model from [%s]\n" % __file__, file=sys.stderr)
            path = os.path.normpath('model/lid.176.ftz.wiki.fasttext')
            LanguageDetectModel.fasttext_model = fasttext.load_model(path)
        return LanguageDetectModel.fasttext_model
