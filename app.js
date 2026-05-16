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

async function loadGridProgress() {

    const response =
        await fetch("gridprogress.json");

    const data = await response.json();

    let html = "<table>";

    html += `
        <tr>
            <th>Month</th>
            <th>Completed</th>
        </tr>
    `;

    data.months.forEach(m => {

        html += `
            <tr>
                <td>${m.month}</td>
                <td>${m.completed}/48</td>
            </tr>
        `;
    });

    html += "</table>";

    document.getElementById(
        "gridProgress"
    ).innerHTML = html;
}

loadGridProgress();