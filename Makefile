all: icons app

icons:
	podman exec -it -v ${PWD}:/var/storage repo.rcmd.space/inkscape-ci:latest /var/storage/generate-pngs.sh

app:
	echo "Building app"
