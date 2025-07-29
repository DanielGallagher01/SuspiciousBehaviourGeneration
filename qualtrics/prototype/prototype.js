let startTime = null;
let timerInterval = null;
let investigating = false;
let stopped = [false, false, false, false, false, false];


Qualtrics.SurveyEngine.addOnReady(function() {
  const startBtn = document.getElementById("startBtn");
  if (startBtn) {
    startBtn.addEventListener("click", function() {
      const vids = document.querySelectorAll(".survey-video");
      vids.forEach((vid, index) => {
        vid.play().catch(err => {
          console.warn(`Video ${index + 1} failed to play:`, err);
        });
      });
	  startBtn.style.display = "none";
	  document.getElementById("investigateBtn").style.display = "block";
	  document.getElementById("timer").style.display = "block";
		
	  //Timer
	  startTime = Date.now();
	  if (timerInterval) clearInterval(timerInterval); // clear if already running

	  timerInterval = setInterval(() => {
		const elapsed = (Date.now() - startTime) / 1000; // seconds
		document.getElementById("timer").textContent = elapsed.toFixed(1) + "s";
	  }, 100); // update every 0.1 second
		});
  }
});

Qualtrics.SurveyEngine.addOnReady(function() {
  const investigateBtn = document.getElementById("investigateBtn");
  if (investigateBtn) {
	  investigateBtn.style.display = "none";
	  investigateBtn.addEventListener("click", function() {
		 console.log("investigate button clicked");
		  investigating = !investigating;
		  if (investigating) {
			  investigateBtn.textContent = "Stop Investigating";
			  const vids = document.querySelectorAll(".survey-video");
			  vids.forEach((vid, index) => {
				vid.pause();
			  });
			  checkIfAllDone();
			  document.getElementById("stopBtn1").style.display = "block";
			  document.getElementById("stopBtn2").style.display = "block";
			  document.getElementById("stopBtn3").style.display = "block";
			  document.getElementById("stopBtn4").style.display = "block";
			  document.getElementById("stopBtn5").style.display = "block";
			  document.getElementById("stopBtn6").style.display = "block";
		  } else {
			  investigateBtn.textContent = "Investigate";
			  const vids = document.querySelectorAll(".survey-video");
			  vids.forEach((vid, index) => {
				if (!stopped[index]) {
					vid.play().catch(err => {
					  console.warn(`Video ${index + 1} failed to play:`, err);
					});
				}
			  });
			  document.getElementById("stopBtn1").style.display = "none";
			  document.getElementById("stopBtn2").style.display = "none";
			  document.getElementById("stopBtn3").style.display = "none";
			  document.getElementById("stopBtn4").style.display = "none";
			  document.getElementById("stopBtn5").style.display = "none";
			  document.getElementById("stopBtn6").style.display = "none";
		  }
		  
	  });
  }
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




Qualtrics.SurveyEngine.addOnload(function() {
    		  document.getElementById("stopBtn1").style.display = "none";
			  document.getElementById("stopBtn2").style.display = "none";
			  document.getElementById("stopBtn3").style.display = "none";
			  document.getElementById("stopBtn4").style.display = "none";
			  document.getElementById("stopBtn5").style.display = "none";
			  document.getElementById("stopBtn6").style.display = "none";
	
	document.getElementById("stopBtn1").addEventListener("click", function() {
		stopped[0] = true;	
		document.getElementById("stopBtn1").style.backgroundColor = "grey";
		checkIfAllDone();
	});
														 
	document.getElementById("stopBtn2").addEventListener("click", function() {
		stopped[1] = true;	
		document.getElementById("stopBtn2").style.backgroundColor = "grey";
		checkIfAllDone();
	});

    document.getElementById("stopBtn3").addEventListener("click", function() {
		stopped[2] = true;	
		document.getElementById("stopBtn3").style.backgroundColor = "grey";
		checkIfAllDone();
	});
														 
	document.getElementById("stopBtn4").addEventListener("click", function() {
		stopped[3] = true;	
		document.getElementById("stopBtn4").style.backgroundColor = "grey";
		checkIfAllDone();
	});
														 
	document.getElementById("stopBtn5").addEventListener("click", function() {
		stopped[4] = true;	
		document.getElementById("stopBtn5").style.backgroundColor = "grey";
		checkIfAllDone();
	});
														 
	document.getElementById("stopBtn6").addEventListener("click", function() {
		stopped[5] = true;	
		document.getElementById("stopBtn6").style.backgroundColor = "grey";
		checkIfAllDone();
	});										 
});


Qualtrics.SurveyEngine.addOnload(function() {
    const vids = document.querySelectorAll(".survey-video");
	vids.forEach((vid, index) => {
	  vid.addEventListener("ended", checkIfAllDone);
	});
});

function checkIfAllDone() {
  const vids = document.querySelectorAll(".survey-video");
  let allDone = true;
  vids.forEach((vid, index) => {
	if(vid.ended) {
		stopped[index] = true;
		
		switch (index) {
			case 0:
				document.getElementById("stopBtn1").style.backgroundColor = "grey";
				break;
			case 1:
				document.getElementById("stopBtn2").style.backgroundColor = "grey";
				break;
			case 2:
				document.getElementById("stopBtn3").style.backgroundColor = "grey";
				break;
			case 3:
				document.getElementById("stopBtn4").style.backgroundColor = "grey";
				break;
			case 4:
				document.getElementById("stopBtn5").style.backgroundColor = "grey";
				break;
			case 5:
				document.getElementById("stopBtn6").style.backgroundColor = "grey";
				break;
		}

	}
    if (!stopped[index] && !vid.ended) {
      allDone = false;
    }
  });

  if (allDone) {
    clearInterval(timerInterval); 
	document.getElementById("stopBtn1").style.display = "none";
	document.getElementById("stopBtn2").style.display = "none";
	document.getElementById("stopBtn3").style.display = "none";
	document.getElementById("stopBtn4").style.display = "none";
	document.getElementById("stopBtn5").style.display = "none";
	document.getElementById("stopBtn6").style.display = "none";
	document.getElementById("investigateBtn").style.display = "none";
    document.getElementById("advanceBtn").style.display = "block";
    console.log("All videos done or stopped. Timer stopped, advance button shown.");
  }
}
