#!/bin/bash
args=("$@")

function usage {	
	echo "usage: runner.sh <option> [inputFile<P>] [inputFile<C>|model.m]"
	echo "	option	: -build	-- maven: build project and jar"
	echo "		: -learn  	-- to run the learner"
	echo "				   needs an inputCSV file"
	echo "	    	: -check  	-- to check the output,"
        echo "	     			   needs a predict<ouput> file and" 
	echo "				   a check file"
        echo "	    	: -predict	-- to run the predictor,"
        echo "	     			   needs a predict<?> file and" 
	echo "				   a model.m file"

	exit 1;
}


function hdfs_conf {
        echo "adding input file to HDFS"
	hdfs dfs -mkdir -p /user/hduser
	hdfs dfs -copyFromLocal $1 /user/hduser/
	hdfs dfs -ls /user/hduser
	echo "removing hdfs output directory [/tmp]"
	hdfs dfs -rm -r /tmp
}

# usage
if [ ${#args[@]} -le "0" ]; then
	usage
fi


# make output dir if doesnt exist
if [ -d output ]; then
	rm -rf output
fi

mkdir output



jarfile="target/a3-0.0.1-SNAPSHOT.jar"

if [ ! -f "$jarfile" ]; then
	echo "jar file does not exist please execute runner with -build option"
	exit 1
fi


case "$1" in
	"-build")
		mvn clean install
		;;
	"-learn")
		if [ ${#args[@]} -ne "2" ]; then
			usage
		fi

		hdfs_conf $2
		filename=$(basename $2)
		echo "preparing to run $1"

		hadoop jar "$jarfile" "$1" "/user/hduser/$filename" /tmp/out
	
		hdfs dfs -getmerge /tmp/out "output/model.m"
		echo "model.m is placed in [output/] dir"
		;;
	"-predict")
		if [ ${#args[@]} -ne "3" ]; then
			usage
		fi
		echo "predictor running"
		;;
	"-check")
		if [ ${#args[@]} -ne "3" ]; then
			usage
		fi
		java -Xmx2048M -cp "$jarfile" "$1" "$2" "$3"
		;;
	*)
		echo usage
esac
