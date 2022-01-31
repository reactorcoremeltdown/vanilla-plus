all: icons app

icons:
	podman run -it -v $(PWD):/var/storage repo.rcmd.space/inkscape-ci:latest /var/storage/generate-pngs.sh

app:
	echo "Building app"
