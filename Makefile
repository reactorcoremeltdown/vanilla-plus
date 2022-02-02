all: publish

icons:
	podman run -it -v $(shell pwd):/var/storage repo.rcmd.space/inkscape-ci:latest /var/storage/generate-pngs.sh
	#echo "temporarily disabled"

app: icons
	echo "Building app"
	test -d /tmp/gradlecache || mkdir /tmp/gradlecache
	podman run -it -v $(shell pwd):/project -v /tmp/gradlecache:"/root/.gradle" mingc/android-build-box bash -c 'cd /project; ./gradlew build'

publish: app
	mv app/build/outputs/apk/release/app-release-unsigned.apk /var/lib/fdroid/unsigned/space.rcmd.android.vanillaplus_$(shell grep versionCode app/src/main/AndroidManifest.xml | cut -f 2 -d '"').apk
	touch /var/lib/fdroid/metadata/space.rcmd.android.vanillaplus.yml
	cd /var/lib/fdroid && fdroid publish --verbose && fdroid update --verbose
