README

Course: Parallel Data Processing in Map Reduce (CS6240)

Assignment A3: Analyzing Airline Data

Authors: Pramod Khare, Ryan Millay, Nikit Waghela, Ankita Muley

Requirement: Before running any scripts please verify if you have following runtime env's/libs installed.
	1) Java: v1.6+ preferable 
	2) Maven: for building projects and creating executable jars
	3) Hadoop (with proper changes to etc/hadoop config xmls for pseudo / standalone mode)
	4) input file for map-reduce program (either locally or in HDFS filesystem)
	
Important files details- Submission/parent directory contains following sub-directories and files- 
	a) src - source directory contains all java source code and resource files,
	b) pom.xml - project object model xml configuration file for building project with Maven, 
	c) run-all-pseudo.sh - automated script to run program in hadoop pseudo distributed mode, 
	e) run-all-standalone.sh - automated script to run program in hadoop standalone mode, and 
	f) README file - this file.

How to execute/run programs: 
	
	1) Running in Hadoop Standalone mode: 
	IMPORTANT NOTE: Make sure your hadoop is not configured to run in pseudo mode, then use next section 
		a) To build and create jar for project: 
		>> ./run-all-standalone.sh -build

		b) To learn the features and prediction model:  (modify command as per your data.csv or all.csv file location)
		>> ./run-all-standalone.sh -learn data.csv
		
		c) To predict flight arrival delays from model.m (modify command as per your model.m and predict.m file location)
		>> ./run-all-standalone.sh -predict output/model.m predict.csv

		d) To check accuracy of predictions (modify command as per your check.csv and predict.m file location)
		>> ./run-all-standalone.sh -check check.csv predict.csv

	2) Running in Hadoop pseudo distributed mode:
	IMPORTANT NOTE: When running in standalone mode, make sure you have modified proper config files under $HADOOP_HOME/etc/hadoop directory and you have formatted and started the dfs and yarn using ./start-dfs.sh and ./start-yarn.sh
		a) To build and create jar for project: 
		>> ./run-all-standalone.sh -build

		b) To learn the features and prediction model:  (modify command as per your data.csv or all.csv file location)
		>> ./run-all-standalone.sh -learn data.csv
		
		c) To predict flight arrival delays from model.m (modify command as per your model.m and predict.m file location)
		>> ./run-all-standalone.sh -predict output/model.m predict.csv

		d) To check accuracy of predictions (modify command as per your check.csv and predict.m file location)
		>> ./run-all-standalone.sh -check check.csv predict.csv

	3) Running on AWS EMR:
		In order to run this project on EMR, please do following things:
		a) Copy target/a3-1.0.jar, data.csv (or all.csv) into your own s3 bucket
		b) Go to AWS console -> EMR -> Cluster List
		c) Click "Create Cluster"
		d) Fill following details in Cluster Configuration - Cluster Name, Termination protection - No, logging Enabled - give your S3 bucket log folder path, debugging - not required.
		e) Under Tags section - No change here, i.e. No tags
		f) Under Software Configuration - Hadoop Distribution - Amazon - AMI Version - 2.4.8, Applications to be installed - none.
		g) under Hardware Configuration - Master, Core - give some values as per your requirements e.g. 1 Master Node - m1.medium, Core nodes	- 3 - m1.medium.
		h) Select your EC2 key-pair
		i) Under IAM Roles -select Default
		j) Under Steps section -> Add and Configure Custom jar --then-> select Auto Terminate = yes.
		k) Configure Custom Jar -- 
			Select jar -- location from your s3 bucket - (i.e. a3-1.0.jar), 
			Arguments -- -learn s3://cs6240.my.bucket/AssignmentA3/all.csv s3://cs6240.my.bucket/AssignmentA3/output/

		l) Click "Create Cluster", this will create the cluster and execute the learn MR job and output would be available in your S3 bucket in given output directory.
		m) In order to run - next 2 parts (predict and check), you would need to copy all "part-r-xxxx" to your computer and use following command to concat all files into model.m file 
			>> car output/part-r-* >output/model.m
		n) In order to predict, run following command (caution - modify command as per your model.m and predict.csv file location)
			>> hadoop jar target/a3-1.0.jar -predict output/model.m predict.csv
		o) In order to check, run following command (caution - modify command as per your check.m and predict.csv file location)
			>> hadoop jar target/a3-1.0.jar -check check.csv predict.csv
			(IMP use above predicted predict.csv)

Important Note:
1) Make sure hadoop is properly installed and configured before you run any of above programs.
