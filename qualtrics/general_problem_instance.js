var stopped = [false, false];
var alarm = [false, false];
var chosenType = ["","",""];
var qobj;
var time = 0;
var currentBehaviour = ""

Qualtrics.SurveyEngine.addOnload(function() {
	console.log("On Load");
	randomiseVideos(); 

	prepareStartButton();
	prepareStopButton();

	prepareVids();
	
	domainArray = 	Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_domain");
	domainArray = domainArray + domain + "|";
	Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_domain", domainArray);
	
	console.log(Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_domain"));
	console.log(Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_behaviour"));
	console.log(Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_map"));
	console.log(Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_stop"));
	console.log(Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_watch"));
	console.log(Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_userinput_watch"));
	console.log(Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_userinput_stop"));
});

Qualtrics.SurveyEngine.addOnPageSubmit(function() {
	userinput_watch = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_userinput_watch");
	userinput_watch = userinput_watch + document.querySelector("#question-QID43 textarea").value + "|";
	Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_userinput_watch", userinput_watch);

	userinput_stop = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_userinput_stop");
	userinput_stop = userinput_stop + document.querySelector("#question-QID55 textarea").value + "|";
	Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_userinput_stop", userinput_stop);
	
	document.querySelector("#question-QID43 textarea").value = "";
	document.querySelector("#question-QID55 textarea").value = "";
});





Qualtrics.SurveyEngine.addOnReady(function() {
	document.getElementById('question-QID43').style.display = 'none';
	 document.getElementById('question-QID55').style.display = 'none';
	
   this.disableNextButton(); 
	qobj = this;
  const advanceBtn = document.getElementById("advanceBtn");
  if (advanceBtn) {
	  advanceBtn.style.display = "none";
	  advanceBtn.addEventListener("click", function() {
	  console.log("adv button clicked");
	  document.getElementById("vid1").pause();
	  document.getElementById("stopBtn1").style.display = "none";
	  document.getElementById("watchBtn1").style.display = "none";
	  qobj.enableNextButton(); 
	  advanceBtn.style.display = "none";
	  document.getElementById('question-QID43').style.display = "block";
	  document.getElementById('question-QID55').style.display = "block";
	  document.querySelector("#question-QID43 textarea").value = "";
	  document.querySelector("#question-QID55 textarea").value = "";
		  
	if(!alarm[0]) {
		watchArray = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_watch");
		watchArray = watchArray + "null" + "|";
		Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_watch", watchArray);	
	}
	  
	 if(!stopped[0]) {
		stopArray = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_stop");
		stopArray = stopArray + "null" + "|";
		Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_stop", stopArray);	
	}
		  
	stopped[0] = true;

	  });
  }
});




function prepareStopButton() {
	document.getElementById("stopBtn1").style.display = "none";
	document.getElementById("watchBtn1").style.display = "none";
	
	document.getElementById("stopBtn1").addEventListener("click", function() {
		if (alarm[0]) {
			document.getElementById("stopBtn1").style.display = "none";
			stopped[0] = true;
			checkIfAllDone();
			document.getElementById("vid1").pause();
			document.getElementById('vid1cont').style.backgroundColor = '#202020';
			
			stopArray = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_stop");
			stopArray = stopArray + time.toString() + "|";
			Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_stop", stopArray);
		}
	});
	
		document.getElementById("watchBtn1").addEventListener("click", function() {
			if (!alarm[0]) {
				document.getElementById("stopBtn1").style.backgroundColor = "red";
				document.getElementById("watchBtn1").style.backgroundColor = "grey";
				alarm[0] = true;
				document.getElementById('vid1cont').style.backgroundColor = '#4444f0';

				watchArray = 	Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_watch");
				watchArray = watchArray + time.toString() + "|";
				Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_watch", watchArray);
			}
		});
			
	
 
}


function prepareVids() {
	const vids = document.querySelectorAll(".survey-video-single");
	vids.forEach((vid, index) => {
	  vid.addEventListener("ended", checkIfAllDone);
	});
}

function randomiseVideos() {
	
		randProblemID = Math.floor(Math.random() * problems.length);
	
		mapArray = 	Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_map");
		mapArray = mapArray + problems[randProblemID].toString() + "|";
		Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_map", mapArray);
	
	    
		instanceID = Math.floor(Math.random() * behaviours.length);
		console.log(instanceID);
	    console.log(behaviours.length);
		console.log("Instance for video:" + behaviours[instanceID]);
	
		behaviourArray = 	Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_behaviour");
		behaviourArray = behaviourArray + behaviours[instanceID].toString() + "|";
		Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_behaviour", behaviourArray);
		currentBehaviour = behaviours[instanceID].toString()
		
		
		
		document.getElementById('vid' + (1) + 'cont').innerHTML = "<video disablepictureinpicture playsinline muted id=\"vid" + (1) + "\" class=\"survey-video-single\">   <source type=\"video/" + fileExtention + "\" src=\"https://github.com/TheAlchemist010/SuspiciousBehaviourGeneration/raw/refs/heads/main/planimation/" + domain + "/" + problems[randProblemID] + "/" + behaviours[instanceID] + "." + fileExtention + "\"> Your browser does not support the video tag. </video> <div class='button-container'>   <button class=\"std-button\" id=\"watchBtn" + (1) + "\">Suspicious</button>   <button class=\"std-button\" id=\"stopBtn" + (1) + "\">⚠Stop⚠</button>   <button id=\"startBtn\" class=\"std-button\">Start</button>   <button id=\"advanceBtn\" class=\"std-button\">Safe</button> </div>";
	  //document.getElementById('stopBtn1').style.margin = 'auto';
	//document.getElementById('stopBtn1').style.margin = 'auto';
	    document.getElementById("stopBtn1").style.backgroundColor = "grey";
		document.getElementById('vid' + (1) + 'cont').style.backgroundColor = '#f0f0f0';
		document.getElementById('vid' + (1) + 'cont').style.padding = '5px';
		document.getElementById('vid' + (1) + 'cont').style.borderRadius = '5px';
		document.getElementById("watchBtn1").style.backgroundColor = "#4444f0";
    	document.getElementById("explantext").innerHTML = explainText;
	
		document.getElementById("titletext").innerHTML = "<h3>" + domain_title + " (" + (6-behaviours.length) + "/5)<\h3>";
	
		if(domain == "logistics") {
				document.getElementById('vid' + (1)).currentTime=4;
		}
  
		
		behaviours.splice(instanceID, 1);
		problems.splice(randProblemID, 1);
		console.log(behaviours);
	    console.log(problems);
	

}

function stopwatch() {
	time++;
	setTimeout(stopwatch, 10);
}

function prepareStartButton() {
	const startBtn = document.getElementById("startBtn");
	startBtn.style.backgroundColor = "blue";
  if (startBtn) {
    startBtn.addEventListener("click", function() {
	  time = 0;
	  stopwatch();
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



	

function checkIfAllDone() {
  const vids = document.querySelectorAll(".survey-video-single");
  let allDone = false;
  needNullStopEntry = false;
  vids.forEach((vid, index) => {
	if(vid.ended) {
		needNullStopEntry = true;
		stopped[index] = true;
		allDone = true;
		
		switch (index) {
			case 0:
				document.getElementById("stopBtn1").style.display = "none";
				document.getElementById("watchBtn1").style.display = "none";
				document.getElementById('vid1cont').style.backgroundColor = '#202020';
				const container = document.getElementById("vid1cont");

				//endtext = ""
				//if (currentBehaviour == "opt-a") {
				//	endtext = "	
				//}
				
				
				container.querySelector("video").replaceWith(
				  Object.assign(document.createElement("div"), {
					className: "end-of-feed",
					innerText: "End of feed"
				  })
				);
				break;
		}

	}
  });

  if (allDone) {
	document.getElementById("stopBtn1").style.display = "none";
	document.getElementById("watchBtn1").style.display = "none";
	  
	if(!alarm[0]) {
		watchArray = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_watch");
		watchArray = watchArray + "null" + "|";
		Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_watch", watchArray);	
	}
	  
	 if(needNullStopEntry) {
		topArray = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_stop");
		stopArray = stopArray + "null" + "|";
		Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_stop", stopArray);	
	}
	  
	  
	qobj.enableNextButton(); 
	advanceBtn.style.display = "none";
	document.getElementById('question-QID43').style.display = "block";
	document.getElementById('question-QID55').style.display = "block";
	  
    document.querySelector("#question-QID43 textarea").value = "";
	document.querySelector("#question-QID55 textarea").value = "";

    console.log("All videos done or stopped.");
  }
}
