#!/bin/sh

LOG_FILE="/var/opt/archie/logs/$(basename $0).log"

imagePreview() {
	IMAGE_COUNT=0
	for f in /var/opt/archie/assetstore/*.$1; do
		id=$(basename $f .$1);
		target=/var/opt/archie/preview/$id.png		
		if [ ! -f $target ]; then
				((IMAGE_COUNT++))
				echo $f;
				convert -auto-orient -resample 72 -resize 550x380\> $f $target;
		fi
	done
	echo "`date` : $IMAGE_COUNT $1 preview files created" >> $LOG_FILE
}

pdfPreview() {
	PDF_COUNT=0
	for f in /var/opt/archie/assetstore/*.pdf; do
		id=$(basename $f .pdf);
		target=/var/opt/archie/preview/$id.png
		if [ ! -f $target ]; then
			((PDF_COUNT++))
			echo $f;
			convert -crop 550x380+12+12 $f[0] $target;
		fi
	done
	echo "`date` : $PDF_COUNT pdf preview files created" >> $LOG_FILE
}

echo "`date` : operation started" >> $LOG_FILE
imagePreview jpeg
imagePreview jpg
pdfPreview
echo "`date` : operation completed" >> $LOG_FILE
echo "***" >> $LOG_FILE
