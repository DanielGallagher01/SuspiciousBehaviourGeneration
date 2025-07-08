const vids = [];

Qualtrics.SurveyEngine.addOnload(function() {
    // Runs when the page loads
});

Qualtrics.SurveyEngine.addOnReady(function() {
    // Runs when the page is fully displayed
    for (let i = 1; i <= 6; i++) {
        const vid = document.getElementById(`vid${i}`);
        if (vid) {
            vids.push(vid);
        }
    }
});

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
    });
  }
});
