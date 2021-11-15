import logging.config
import os
import argparse
from lang_detect import LanguageDetect
from datetime import datetime

logging_conf_path = os.path.normpath(os.path.join(os.path.dirname(__file__), 'logging.conf'))
logging.config.fileConfig(logging_conf_path)
log = logging.getLogger(__name__)


def main():
    parser = argparse.ArgumentParser(conflict_handler="resolve")
    parser.add_argument("--lang-id", dest="lang_id", action="store_true", default=False)
    parser.add_argument("-t", "--text", help="input text", default="Hello world!")

    args = parser.parse_args()
    if args.lang_id:
        ret = LanguageDetect.lang_id(args.text, 3)
        log.info("Language id [%s]", ret)


if __name__ == "__main__":
    main()