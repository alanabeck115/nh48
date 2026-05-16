async function loadPeaks() {

    const response = await fetch("mountains.json");

    const peaks = await response.json();

    const tbody = document.querySelector("#peakTable tbody");

    peaks.forEach(peak => {

        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${peak.name}</td>
            <td>${peak.elevation}</td>
            <td>${peak.date}</td>
            <td>${peak.winter ? "Yes" : "No"}</td>
        `;

        tbody.appendChild(row);
    });

    document.getElementById("summary").innerHTML =
        `<h2>${peaks.length} Peaks Completed</h2>`;
}

loadPeaks();