var stopped = false;
var watch = false;
var safe = false;
var qobj;

var chosenType = ["", "", ""];
var currentBehaviour = ""

Qualtrics.SurveyEngine.addOnload(function () {
	console.log("On Load");
	qobj = this;

	randomiseVideos();

	prepareStartButton();
	prepareStopButton();
	prepareSafeButton(this);

	prepareVids();

	hideAndClearFollowup();
	this.disableNextButton();

	domainArray = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_domain");
	domainArray = domainArray + domain + "|";
	Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_domain", domainArray);

	console.log("Domains:" + Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_domain"));
	console.log("Behaviours:" + Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_behaviour"));
	console.log("Maps:" + Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_map"));
	console.log("Stop times:" + Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_stop"));
	console.log("Watch times:" + Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_watch"));
	console.log("Safe Times:" + Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_safe"));
	console.log("Watch text:" + Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_userinput_watch"));
	console.log("Stop text:" + Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_userinput_stop"));
	console.log("NotWatch text:" + Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_userinput_notwatch"));
	console.log("NotStop text:" + Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_userinput_notstop"));
	console.log("Safe text:" + Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_userinput_safe"));
});

Qualtrics.SurveyEngine.addOnPageSubmit(function () {
	userinput_watch = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_userinput_watch");
	userinput_watch = userinput_watch + document.querySelector("#question-QID43 textarea").value + "|";
	Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_userinput_watch", userinput_watch);

	userinput_stop = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_userinput_stop");
	userinput_stop = userinput_stop + document.querySelector("#question-QID55 textarea").value + "|";
	Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_userinput_stop", userinput_stop);

	userinput_notwatch = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_userinput_notwatch");
	userinput_notwatch = userinput_notwatch + document.querySelector("#question-QID101 textarea").value + "|";
	Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_userinput_notwatch", userinput_notwatch);

	userinput_notstop = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_userinput_notstop");
	userinput_notstop = userinput_notstop + document.querySelector("#question-QID102 textarea").value + "|";
	Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_userinput_notstop", userinput_notstop);

	userinput_safe = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_userinput_safe");
	userinput_safe = userinput_safe + document.querySelector("#question-QID103 textarea").value + "|";
	Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_userinput_safe", userinput_safe);

	hideAndClearFollowup();
});


function prepareSafeButton() {
	advanceBtn.style.display = "none";
	advanceBtn.addEventListener("click", function () {
		safe = true;
		document.getElementById("vid1").pause();
		console.log("Safe button clicked");
		allDone();
	});
}

function hideAndClearFollowup() {
	document.getElementById('question-QID43').style.display = 'none';
	document.getElementById('question-QID55').style.display = 'none';
	document.getElementById('question-QID102').style.display = 'none';
	document.getElementById('question-QID101').style.display = 'none';
	document.getElementById('question-QID103').style.display = 'none';

	document.querySelector("#question-QID43 textarea").value = "";
	document.querySelector("#question-QID55 textarea").value = "";
	document.querySelector("#question-QID102 textarea").value = "";
	document.querySelector("#question-QID101 textarea").value = "";
	document.querySelector("#question-QID103 textarea").value = "";
}

function showFollowUp() {
	hideAndClearFollowup();

	if (watch) {
		//Why did you press "Suspicious"?
		document.getElementById('question-QID43').style.display = 'block';

		if (stopped) {
			//Why did you choose to stop the agent? 
			document.getElementById('question-QID55').style.display = 'block';
		} else {
			//Why didn't you choose to stop the agent?
			document.getElementById('question-QID102').style.display = 'block';
		}
	} else {
		//Why didn't you press "Suspicious"? 
		document.getElementById('question-QID101').style.display = 'block';
	}



	if (safe) {
		//Why did you press "Safe"?
		document.getElementById('question-QID103').style.display = 'block';
	}
}




function prepareStopButton() {
	document.getElementById("stopBtn1").style.display = "none";
	document.getElementById("watchBtn1").style.display = "none";

	const video = document.getElementById("vid1");

	document.getElementById("stopBtn1").addEventListener("click", function () {
		if (watch) {
			document.getElementById("stopBtn1").style.display = "none";
			stopped = true;
			video.pause();
			allDone();

			document.getElementById('vid1cont').style.backgroundColor = '#202020';

			// Record stop time in seconds
			const stopTimeSec = Math.round(video.currentTime*100);
			stopArray = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_stop");
			stopArray = stopArray + stopTimeSec.toString() + "|";
			Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_stop", stopArray);
		}
	});

	document.getElementById("watchBtn1").addEventListener("click", function () {
		if (!watch) {
			document.getElementById("stopBtn1").style.backgroundColor = "red";
			document.getElementById("watchBtn1").style.backgroundColor = "grey";
			watch = true;
			document.getElementById('vid1cont').style.backgroundColor = '#4444f0';

			// Record watch time in seconds
			const watchTimeSec = Math.round(video.currentTime*100); // hundredths of a second
			watchArray = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_watch");
			watchArray = watchArray + watchTimeSec.toString() + "|";
			Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_watch", watchArray);
		}
	});
}



function prepareVids() {
	const vids = document.querySelectorAll(".survey-video-single");
	vids.forEach((vid, index) => {
		vid.addEventListener("ended", allDone);
	});
}

function randomiseVideos() {

	randProblemID = Math.floor(Math.random() * problems.length);

	mapArray = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_map");
	mapArray = mapArray + problems[randProblemID].toString() + "|";
	Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_map", mapArray);


	instanceID = Math.floor(Math.random() * behaviours.length);
	console.log(instanceID);
	console.log(behaviours.length);
	console.log("Instance for video:" + behaviours[instanceID]);

	behaviourArray = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_behaviour");
	behaviourArray = behaviourArray + behaviours[instanceID].toString() + "|";
	Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_behaviour", behaviourArray);
	currentBehaviour = behaviours[instanceID].toString()



	document.getElementById('vid' + (1) + 'cont').innerHTML = "<video disablepictureinpicture playsinline muted id=\"vid" + (1) + "\" class=\"survey-video-single\">   <source type=\"video/" + fileExtention + "\" src=\"https://github.com/DanielGallagher01/SuspiciousBehaviourGeneration/raw/refs/heads/main/planimation/" + domain + "/" + problems[randProblemID] + "/" + behaviours[instanceID] + "." + fileExtention + "\"> Your browser does not support the video tag. </video> <div class='button-container'>   <button class=\"std-button\" id=\"watchBtn" + (1) + "\">Suspicious</button>   <button class=\"std-button\" id=\"stopBtn" + (1) + "\">⚠Stop⚠</button>   <button id=\"startBtn\" class=\"std-button\">Start</button>   <button id=\"advanceBtn\" class=\"std-button\">Safe</button> </div>";
	document.getElementById("stopBtn1").style.backgroundColor = "grey";
	document.getElementById('vid' + (1) + 'cont').style.backgroundColor = '#f0f0f0';
	document.getElementById('vid' + (1) + 'cont').style.padding = '5px';
	document.getElementById('vid' + (1) + 'cont').style.borderRadius = '5px';
	document.getElementById("watchBtn1").style.backgroundColor = "#4444f0";
	document.getElementById("explantext").innerHTML = explainText;

	document.getElementById("titletext").innerHTML = "<h3>" + domain_title + " (" + (6 - behaviours.length) + "/5)<\h3>";

	if (domain == "logistics") {
		document.getElementById('vid' + (1)).currentTime = 4;
	}


	behaviours.splice(instanceID, 1);
	problems.splice(randProblemID, 1);
	console.log(behaviours);
	console.log(problems);


}


function prepareStartButton() {
	const startBtn = document.getElementById("startBtn");
	startBtn.style.backgroundColor = "blue";
	if (startBtn) {
		startBtn.addEventListener("click", function () {
			const vids = document.querySelectorAll(".survey-video-single");
			vids.forEach((vid, index) => {
				vid.play().catch(err => {
					console.warn(`Video ${index + 1} failed to play:`, err);
				});
				vid.playbackRate = 0.8;
			});
			startBtn.style.display = "none";
			document.getElementById("advanceBtn").style.display = "block";
			document.getElementById("advanceBtn").style.backgroundColor = "green";
			document.getElementById("stopBtn1").style.display = "block";
			document.getElementById("watchBtn1").style.display = "block";
		});
	}
}




function allDone() {
	document.getElementById("stopBtn1").style.display = "none";
	document.getElementById("watchBtn1").style.display = "none";
	document.getElementById("advanceBtn").style.display = "none";

	if (!watch) {
		let watchArray = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_watch");
		watchArray = watchArray + "null" + "|";
		Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_watch", watchArray);
	}

	if (!stopped) {
		let stopArray = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_stop");
		stopArray = stopArray + "null" + "|";
		Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_stop", stopArray);
	}

	let safeArray = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_safe");
	if (safe) {
		const video = document.getElementById("vid1");
		const watchTimeSec = Math.round(video.currentTime*100);
		safeArray = safeArray + watchTimeSec.toString() + "|";
	} else {
		safeArray = safeArray + "null" + "|";
	}
	Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_safe", safeArray);

	if (!stopped && !watch && !safe) {
		const container = document.getElementById("vid1cont");
		container.querySelector("video").replaceWith(
			Object.assign(document.createElement("div"), {
				className: "end-of-feed",
				innerText: "End of feed"
			})
		);
	}



	qobj.enableNextButton();
	
	showFollowUp();

	console.log("All videos done or stopped.");

}
