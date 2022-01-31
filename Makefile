all: icons app

icons:
	podman run -it -v $(shell pwd):/var/storage repo.rcmd.space/inkscape-ci:latest /var/storage/generate-pngs.sh

app:
	echo "Building app"
	test -d /tmp/gradlecache || mkdir /tmp/gradlecache
	podman run -it -v $(shell pwd):/project -v /tmp/gradlecache:"/root/.gradle" mingc/android-build-box bash -c 'cd /project; ./gradlew build'
