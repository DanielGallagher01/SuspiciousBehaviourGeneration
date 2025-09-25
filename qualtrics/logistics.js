var domain = "logistics";

var problems = ["Logistics-A", "Logistics-B", "Logistics-C", "Logistics-D", "Logistics-E"];

var behaviours = ["adv", "amb", "opt-a", "opt-d", "loi"];

var fileExtention = "mp4";

var explainText = "You are employed to monitor delivery operations across three major cities. Recall that green dots indicate destinations where packages are to be delivered. While trucks and planes can carry any number of packages, packages of all three colours (containing each of the three different chemicals) should never be delivered to any one destination.<br><br><b>If you observe anything suspicious, click SUSPICIOUS</b>.This will give ground staff the opportunity to track down the person or persons responsible. It will also enable the STOP button.<b> Click STOP to deactivate a truck directly or to require that a plane turn back. Pilots and truck drivers are aware that they may be watched.<b> If you are satisfied that the situation is under control, click SAFE to move on.</b><br><br><b>Press START to begin.</b>";

Qualtrics.SurveyEngine.addOnload(function() {
	domainNum = 	parseInt(Qualtrics.SurveyEngine.getJSEmbeddedData("domains_completed"));
	domainNum += 1
	Qualtrics.SurveyEngine.setJSEmbeddedData("domains_completed", domainNum);
});

domain_title = "Delivery Domain"
