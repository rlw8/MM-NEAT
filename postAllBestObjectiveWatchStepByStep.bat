REM Usage:   postBestObjectiveWatch.bat <experiment directory> <log prefix> <run type> <run number> <number of trials per individual>
REM Example: postBestObjectiveWatch.bat onelifeconflict OneLifeConflict OneModule 0 5
java -jar "dist/MM-NEATv2.jar" runNumber:%4 parallelEvaluations:false base:%1 log:%2-%3 saveTo:%3 trials:%5 io:false netio:false onlyWatchPareto:true ucb1Evaluation:false experiment:edu.utexas.cs.nn.experiment.post.ObjectiveBestNetworksExperiment logLock:true watchLastBest:false monitorSubstrates:true showVizDoomInputs:true showCPPN:true stepByStep:true substrateGridSize:10 showHighestActivatedOutput:true sortOutputActivations:true inheritFitness:false watch:true showNetworks:true printFitness:true animateNetwork:false showSubnetAnalysis:true monitorInputs:true 
