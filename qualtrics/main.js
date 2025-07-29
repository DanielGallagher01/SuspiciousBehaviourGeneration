let startTime = null;
let timerInterval = null;
let investigating = false;
let stopped = [false, false];
let alarm = [false, false];

let agentTypes = ["opt-a", "opt-d", "amb", "loi", "adv"];

 
Qualtrics.SurveyEngine.addOnload(function() {
	randomiseVideos();
});
								  
Qualtrics.SurveyEngine.addOnReady(function() {

	prepareStartButton();
	prepareStopButton();
	
	prepareVids();
});



Qualtrics.SurveyEngine.addOnReady(function() {
   this.disableNextButton(); 
	var qobj = this;
  const advanceBtn = document.getElementById("advanceBtn");
  if (advanceBtn) {
	  advanceBtn.style.display = "none";
	  advanceBtn.addEventListener("click", function() {
		 console.log("adv button clicked");
		qobj.enableNextButton(); 
    	qobj.clickNextButton(); 
	  });
  }
});




function prepareStopButton() {
	document.getElementById("stopBtn1").style.display = "none";
	document.getElementById("stopBtn2").style.display = "none";
	document.getElementById("stopBtn3").style.display = "none";
	
	document.getElementById("stopBtn1").addEventListener("click", function() {
		if (alarm[0]) {
			document.getElementById("stopBtn1").style.display = "none";
			checkIfAllDone();
			stopped[0] = true;
			document.getElementById("vid1").pause();
			document.getElementById('vid1cont').style.backgroundColor = '#202020';
		} else {
			document.getElementById("stopBtn1").style.backgroundColor = "red";
			document.getElementById("stopBtn1").textContent = "Stop";
			alarm[0] = true;
			document.getElementById('vid1cont').style.backgroundColor = '#f04444';
		}
	});
														 
	document.getElementById("stopBtn2").addEventListener("click", function() {
		if (alarm[1]) {
			document.getElementById("stopBtn2").style.display = "none";
			checkIfAllDone();
			stopped[1] = true;
			document.getElementById("vid2").pause();
			document.getElementById('vid2cont').style.backgroundColor = '#202020';
		} else {
			document.getElementById("stopBtn2").style.backgroundColor = "red";
			document.getElementById("stopBtn2").textContent = "Stop";
			alarm[1] = true;
			document.getElementById('vid2cont').style.backgroundColor = '#f04444';
		}
	});
	
	document.getElementById("stopBtn3").addEventListener("click", function() {
		if (alarm[2]) {
			document.getElementById("stopBtn3").style.display = "none";
			checkIfAllDone();
			stopped[2] = true;
			document.getElementById("vid3").pause();
			document.getElementById('vid3cont').style.backgroundColor = '#202020';
		} else {
			document.getElementById("stopBtn3").style.backgroundColor = "red";
			document.getElementById("stopBtn3").textContent = "Stop";
			alarm[2] = true;
			document.getElementById('vid3cont').style.backgroundColor = '#f04444';
		}
	});
 
}


function prepareVids() {
	const vids = document.querySelectorAll(".survey-video");
	vids.forEach((vid, index) => {
	  vid.addEventListener("ended", checkIfAllDone);
	});
}

function randomiseVideos() {
	rand = Math.floor(Math.random() * 5);
	console.log("Problem A: " + agentTypes[rand]);
	
	document.getElementById('vid1cont').innerHTML = "<video disablepictureinpicture=\"\" playsinline=\"\" muted id=\"vid1\" class=\"survey-video\"> <source type=\"video/mp4\" src=\"https://github.com/TheAlchemist010/SuspiciousBehaviourGeneration/raw/refs/heads/main/planimation/navigation/Bank-A/"+agentTypes[rand]+".mp4\"> Your browser does not support the video tag. </video> <button class=\"std-button\" id=\"stopBtn1\">Raise Alarm</button>";
	document.getElementById('vid1cont').style.backgroundColor = '#f0f0f0';
	document.getElementById('vid1cont').style.padding = '5px';
	document.getElementById('vid1cont').style.borderRadius = '5px';
	
	rand = Math.floor(Math.random() * 5);
	console.log("Problem B: " + agentTypes[rand]);
	
	document.getElementById('vid2cont').innerHTML = "<video disablepictureinpicture=\"\" playsinline=\"\" muted id=\"vid2\" class=\"survey-video\"> <source type=\"video/mp4\" src=\"https://github.com/TheAlchemist010/SuspiciousBehaviourGeneration/raw/refs/heads/main/planimation/navigation/Bank-B/"+agentTypes[rand]+".mp4\"> Your browser does not support the video tag. </video> <button class=\"std-button\" id=\"stopBtn2\">Raise Alarm</button>";
	document.getElementById('vid2cont').style.backgroundColor = '#f0f0f0';
	document.getElementById('vid2cont').style.padding = '5px';
	document.getElementById('vid2cont').style.borderRadius = '5px';
	
	rand = Math.floor(Math.random() * 5);
	console.log("Problem C: " + agentTypes[rand]);
	
	document.getElementById('vid3cont').innerHTML = "<video disablepictureinpicture=\"\" playsinline=\"\" muted id=\"vid3\" class=\"survey-video\"> <source type=\"video/mp4\" src=\"https://github.com/TheAlchemist010/SuspiciousBehaviourGeneration/raw/refs/heads/main/planimation/navigation/Bank-C/"+agentTypes[rand]+".mp4\"> Your browser does not support the video tag. </video> <button class=\"std-button\" id=\"stopBtn3\">Raise Alarm</button>";
	document.getElementById('vid3cont').style.backgroundColor = '#f0f0f0';
	document.getElementById('vid3cont').style.padding = '5px';
	document.getElementById('vid3cont').style.borderRadius = '5px';
}

function prepareStartButton() {
	const startBtn = document.getElementById("startBtn");
  if (startBtn) {
    startBtn.addEventListener("click", function() {
      const vids = document.querySelectorAll(".survey-video");
      vids.forEach((vid, index) => {
        vid.play().catch(err => {
          console.warn(`Video ${index + 1} failed to play:`, err);
        });
		 vid.playbackRate = 0.8; 
      });
	  startBtn.style.display = "none";		
	  document.getElementById("advanceBtn").style.display = "block";
	  document.getElementById("stopBtn1").style.display = "block";
	  document.getElementById("stopBtn2").style.display = "block";
	  document.getElementById("stopBtn3").style.display = "block";
	});
  }
}

function checkIfAllDone() {
  const vids = document.querySelectorAll(".survey-video");
  let allDone = true;
  vids.forEach((vid, index) => {
	if(vid.ended) {
		stopped[index] = true;
		
		switch (index) {
			case 0:
				document.getElementById("stopBtn1").style.display = "none";
				document.getElementById('vid1cont').style.backgroundColor = '#202020';
				break;
			case 1:
				document.getElementById("stopBtn2").style.display = "none";
				document.getElementById('vid2cont').style.backgroundColor = '#202020';
				break;
			case 2:
				document.getElementById('vid3cont').style.backgroundColor = '#202020';
				document.getElementById("stopBtn3").style.display = "none";
				break;
		}

	}
    if (!stopped[index] && !vid.ended) {
      allDone = false;
    }
  });

  if (allDone) {
	document.getElementById("stopBtn1").style.display = "none";
	document.getElementById("stopBtn2").style.display = "none";
	document.getElementById("stopBtn3").style.display = "none";
    console.log("All videos done or stopped. Timer stopped, advance button shown.");
  }
}
