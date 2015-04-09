#!/bin/bash

# Usage details
function usage {	
	echo "usage: ./run-all.sh <option> [inputFile<P>] [inputFile<C>|model.m]"
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

# Function to copy file from local-file-system to HDFS
function hdfs_conf {
        echo "adding input file to HDFS"
	hdfs dfs -mkdir -p /user/"$username"
	hdfs dfs -copyFromLocal $1 /user/"$username"/
	hdfs dfs -ls /user/"$username"
	echo "removing hdfs output directory [/tmp]"
	hdfs dfs -rm -r output
}

args=("$@")
username=$(whoami)
jarfile="target/a3-1.0.jar"

# usage
if [ ${#args[@]} -le "0" ]; then
	usage
fi

if [ "$1" != "-build" ] && [ ! -f "$jarfile" ]; then
	echo "jar file does not exist please execute runner with -build option"
	exit 1
fi

case "$1" in
	"-build")
		mvn clean install
		;;
	"-learn")
		if [ ${#args[@]} -lt "2" ]; then
			usage
		fi
			
		# make output dir if doesnt exist
		if [ -d output ]; then
			rm -rf output
		fi

		# make output dir if doesnt exist, else delete old one
		if [ -d output ]; then
			hdfs dfs -rmr output
		fi
		
		hdfs_conf $2
		filename=$(basename $2)

		# Execute the Learn MR job in pseudo mode 
		hadoop jar target/a3-1.0.jar -learn "/user/$username/$filename" output

		# merge all the output parts into single model.m file
		hdfs dfs -getmerge output/part-r-* "output/model.m"
		echo "model.m is placed in [output/] dir"
		;;
	"-predict")
		if [ ${#args[@]} -lt "3" ]; then
			usage
		fi
		echo "running predictions"
		hadoop jar target/a3-1.0.jar -predict "$2" "$3"
		;;

	"-check")
		if [ ${#args[@]} -lt "3" ]; then
			usage
		fi
		echo "checking predictions for accuracy"
		hadoop jar target/a3-1.0.jar -check "$2" "$3"	
		;;
	*)
		echo usage
esac
