#!/usr/bin/env bash
args=("$@")

function usage {	
	echo "usage: ./run-all-standalone.sh <option> [inputFile<P>] [inputFile<C>]"
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

# usage
if [ ${#args[@]} -le "0" ]; then
	usage
fi

jarfile="target/a3-1.0.jar"

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
		
		# make output dir if doesnt exist, else delete old one
		if [ -d output ]; then
			rm -rf output
		fi
		
		echo "preparing to run $1"
		hadoop jar target/a3-1.0.jar -learn "$2" output
		cat output/part-r-* >output/model.m
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
