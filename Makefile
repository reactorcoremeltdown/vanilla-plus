all: icons app

icons:
	podman run -it -v $(shell pwd):/var/storage repo.rcmd.space/inkscape-ci:latest /var/storage/generate-pngs.sh

app:
	echo "Building app"
