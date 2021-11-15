#!/usr/bin/env bash
SCRIPT=$(readlink -f ${0})
SCRIPTPATH=$(dirname $SCRIPT)
echo $SCRIPTPATH
if [ ! -d "${SCRIPTPATH}/venv" ]; then
  echo "Generating virtual environment..."
  python3 -m venv "${SCRIPTPATH}/venv"
  echo "Generated virtual environment... ${SCRIPTPATH}/venv"
  . "${SCRIPTPATH}/venv/bin/activate"
  python3 -m pip install --upgrade pip
  pip3 install fasttext
fi

. "${SCRIPTPATH}/venv/bin/activate"

PREV_PWD=$(pwd)

# we have to set current dir to be able to find model/fastext_model_file .
cd "${SCRIPTPATH}" || exit

if [ -f "${SCRIPTPATH}/target/snakejar-example-1.0.0-SNAPSHOT-runner" ]; then
  ${SCRIPTPATH}/target/snakejar-example-1.0.0-SNAPSHOT-runner
else
  ${SCRIPTPATH}/snakejar-example-1.0.0-SNAPSHOT-runner
fi

cd "${PREV_PWD}" || exit
