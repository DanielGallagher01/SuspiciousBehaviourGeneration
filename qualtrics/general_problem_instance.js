var stopped = [false, false];
var alarm = [false, false];
var chosenType = ["","",""];
var qobj;
var time = 0;

Qualtrics.SurveyEngine.addOnload(function() {
	console.log("On Load");
	randomiseVideos();

	prepareStartButton();
	prepareStopButton();

	prepareVids();
	
	domainArray = 	Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_domain");
	domainArray = domainArray + domain + ",";
	Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_domain", domainArray);
	
	console.log(Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_domain"));
	console.log(Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_behaviour"));
	console.log(Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_map"));
	console.log(Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_stop"));
	console.log(Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_watch"));
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
		qobj.enableNextButton(); 
		  advanceBtn.style.display = "none";
	   document.getElementById('question-QID43').style.display = "block";
	   document.getElementById('question-QID55').style.display = "block";
	   document.querySelector("#question-QID43 textarea").value = "";
	   document.querySelector("#question-QID55 textarea").value = "";
    	//qobj.clickNextButton(); 
		  
	if(!alarm[0]) {
		watchArray = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_watch");
		watchArray = watchArray + "null" + ",";
		Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_watch", watchArray);	
	}
	  
	 if(!stopped[0]) {
		stopArray = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_stop");
		stopArray = stopArray + "null" + ",";
		Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_stop", stopArray);	
	}
		  
	stopped[0] = true;

	  });
  }
});




function prepareStopButton() {
	document.getElementById("stopBtn1").style.display = "none";
	
	document.getElementById("stopBtn1").addEventListener("click", function() {
		if (alarm[0]) {
			document.getElementById("stopBtn1").style.display = "none";
			stopped[0] = true;
			checkIfAllDone();
			document.getElementById("vid1").pause();
			document.getElementById('vid1cont').style.backgroundColor = '#202020';
			
			stopArray = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_stop");
			stopArray = stopArray + time.toString() + ",";
			Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_stop", stopArray);
		} else {
			document.getElementById("stopBtn1").style.backgroundColor = "red";
			document.getElementById("stopBtn1").textContent = "⚠Stop⚠";
			alarm[0] = true;
			document.getElementById('vid1cont').style.backgroundColor = '#4444f0';
			
			watchArray = 	Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_watch");
			watchArray = watchArray + time.toString() + ",";
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
		mapArray = mapArray + problems[randProblemID].toString() + ",";
		Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_map", mapArray);
	
	    
		instanceID = Math.floor(Math.random() * behaviours.length);
		console.log(instanceID);
	    console.log(behaviours.length);
		console.log("Instance for video:" + behaviours[instanceID]);
	
		behaviourArray = 	Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_behaviour");
		behaviourArray = behaviourArray + behaviours[instanceID].toString() + ",";
		Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_behaviour", behaviourArray);
		
		
		
		document.getElementById('vid' + (1) + 'cont').innerHTML = "<video disablepictureinpicture=\"\" playsinline=\"\" muted id=\"vid" + (1) + "\" class=\"survey-video-single\"> <source type=\"video/" + fileExtention + "\" src=\"https://github.com/TheAlchemist010/SuspiciousBehaviourGeneration/raw/refs/heads/main/planimation/" + domain + "/" + problems[randProblemID] + "/" + behaviours[instanceID] + "." + fileExtention + "\"> Your browser does not support the video tag. </video> <button class=\"std-button\" id=\"stopBtn" + (1) + "\">🔍Watch🔍</button>";
	  document.getElementById('stopBtn1').style.margin = 'auto';
		document.getElementById('vid' + (1) + 'cont').style.backgroundColor = '#f0f0f0';
		document.getElementById('vid' + (1) + 'cont').style.padding = '5px';
		document.getElementById('vid' + (1) + 'cont').style.borderRadius = '5px';
    document.getElementById("explantext").innerHTML = explainText;
  
		
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
	});
  }
}



	

function checkIfAllDone() {
  const vids = document.querySelectorAll(".survey-video-single");
  let allDone = true;
  vids.forEach((vid, index) => {
	if(vid.ended) {
		stopped[index] = true;
		
		switch (index) {
			case 0:
				document.getElementById("stopBtn1").style.display = "none";
				document.getElementById('vid1cont').style.backgroundColor = '#202020';
				break;
		}

	}
    if (!stopped[index] && !vid.ended) {
      allDone = false;
    }
  });

  if (allDone) {
	document.getElementById("stopBtn1").style.display = "none";
	  
	if(!alarm[0]) {
		watchArray = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_watch");
		watchArray = watchArray + "null" + ",";
		Qualtrics.SurveyEngine.setJSEmbeddedData("Instance_watch", watchArray);	
	}
	  
	 if(!stopped[0]) {
		topArray = Qualtrics.SurveyEngine.getJSEmbeddedData("Instance_stop");
		stopArray = stopArray + "null" + ",";
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
