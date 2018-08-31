#!/bin/bash
#$1 type
#$2 distribution directory, default: /usr/local/red5
if [ -z $2 ]; then
	dirName="/usr/local/red5"
else
	dirName=$2 || "/usr/local/red5/"
fi

echo "Installing Red5 into directory $dirName..."

sudo rm -r $dirName/lib $dirName/plugins

cd ../red5-io
mvn clean
mvn -Dmaven.test.skip=true install || exit 1;

cd ../red5-server-common
#mvn clean
mvn -Dmaven.test.skip=true install || exit 1

cd ../red5-server
mvn clean
mvn -Dmaven.test.skip=true package -P assemble || exit 1 
cd target/
unzip -x red5-server-*.zip
cd red5-server/

sudo mkdir $dirName                                                                                                                                                                                                               
sudo mkdir $dirName/lib                                                                                                                                                                                                           
sudo mkdir $dirName/plugins 

case $1 in

    update)
    `sudo find . -name "*.jar" -exec cp {} $dirName/{} \;`
    ;;
    
    dist)
    `sudo cp -R . $dirName`
    ;;

    *)
    echo 'Action is missing';
    ;;
esac
