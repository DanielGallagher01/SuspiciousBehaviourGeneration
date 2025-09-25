var domain = "navigation";

var problems = ["Bank-A", "Bank-B", "Bank-C", "Bank-D", "Bank-E"];

var instances = [["adv", "amb", "opt-a", "opt-d", "loi"],
				 ["adv", "amb", "opt-a", "opt-d", "loi"],
				 ["adv", "amb", "opt-a", "opt-d", "loi"]];

var behaviours = ["adv", "amb", "opt-a", "opt-d", "loi"];

var fileExtention = "mp4";

var explainText = "You are employed to monitor CCTV on one of the hospital floors. Recall that green squares are charging points (valid robot destinations) red squares are MRI machines.<br><br><b>If a robot’s behaviour seems suspicious, click SUSPICIOUS</b>.This will give security the opportunity to track down the person or persons responsible. It will also enable the STOP button.<b> Click STOP to directly deactivate the robot.</b> Robots are aware that they may be watched.<b> If you are satisfied that the situation is under control, click SAFE to move on.</b><br><br><b>Press START to begin.</b>";

domain_title = "Hospital Domain"


Qualtrics.SurveyEngine.addOnload(function() {
	domainNum = 	parseInt(Qualtrics.SurveyEngine.getJSEmbeddedData("domains_completed"));
	domainNum += 1
	console.log(domainNum)
	Qualtrics.SurveyEngine.setJSEmbeddedData("domains_completed", domainNum);
});
