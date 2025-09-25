var domain = "sokoban";

var problems = ["Sokoban-A", "Sokoban-B", "Sokoban-C", "Sokoban-D", "Sokoban-E"];

var behaviours = ["adv", "amb", "opt-a", "opt-d", "loi"];

var fileExtention = "mp4";

var explainText = "You are employed to monitor CCTV in one of the warehouses. Recall that green dots are storage areas (valid robot destinations) and boxes should not be pushed through doorways.<br><br><b>If a robot’s behaviour seems suspicious, click SUSPICIOUS</b>.This will give investigators the opportunity to track down the person or persons responsible. It will also enable the STOP button. <b>Click STOP to directly deactivate the robot.</b> Robots are aware that they may be watched.<b> If you are satisfied that the situation is under control, click SAFE to move on.</b><br><br><b>Press START to begin.</b>";

Qualtrics.SurveyEngine.addOnload(function() {
	domainNum = 	parseInt(Qualtrics.SurveyEngine.getJSEmbeddedData("domains_completed"));
	domainNum += 1
	Qualtrics.SurveyEngine.setJSEmbeddedData("domains_completed", domainNum);
});

domain_title = "Warehouse Domain"
