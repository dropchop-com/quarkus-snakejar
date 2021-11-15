#!/usr/bin/env bash
echo -e "\nLanguage detected is:\n"
curl -w "\n\n" "http://localhost:8080/hello/lang_id?text=Nadzorniki%20Gen%20energije%20so%2C%20neuradno%2C%20predlagali%20dva%20kandidata%20za%20predsednika%20uprave%20Gen-I."
ab -n 100000 -c 10 "http://localhost:8080/hello/lang_id?text=Nadzorniki%20Gen%20energije%20so%2C%20neuradno%2C%20predlagali%20dva%20kandidata%20za%20predsednika%20uprave%20Gen-I."
