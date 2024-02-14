document.addEventListener("DOMContentLoaded", function() {
    var employmentContractControl = document.getElementById("employmentContract");
    var reasonForDefiniteControl = document.getElementById("reasonForDefinite");
    var dateOfSignOutControl = document.getElementById("dateOfSignOut");
    var foreignNationalCheckbox = document.getElementById('foreignNational');
    var expiryDateOfWorkPermitInput = document.getElementById('expiryDateOfWorkPermit');
    var workingHoursControl = document.getElementById("workingHours");
    var hoursForPartTimeControl = document.getElementById("hoursForPartTime");

    function toggleReasonForDefinite() {
        if (employmentContractControl.value === "Određeno") {
            reasonForDefiniteControl.disabled = false;
            dateOfSignOutControl.disabled = false;
        } else {
            reasonForDefiniteControl.disabled = true;
            dateOfSignOutControl.disabled = true;
        }
    }

    function toggleExpiryDateOfWorkPermitInput() {
        if (foreignNationalCheckbox.checked) {
            expiryDateOfWorkPermitInput.disabled = false;
        } else {
            expiryDateOfWorkPermitInput.disabled = true;
            expiryDateOfWorkPermitInput.value = "";
        }
    }

    function toggleReasonForHoursForPartTime() {
        if (workingHoursControl.value === "Nepuno") {
            hoursForPartTimeControl.disabled = false;
        } else {
            hoursForPartTimeControl.disabled = true;
        }
    }

    toggleReasonForDefinite();
    toggleExpiryDateOfWorkPermitInput();
    toggleReasonForHoursForPartTime()

    employmentContractControl.addEventListener("change", toggleReasonForDefinite);
    foreignNationalCheckbox.addEventListener('change', toggleExpiryDateOfWorkPermitInput);
    workingHoursControl.addEventListener("change", toggleReasonForHoursForPartTime)
});
