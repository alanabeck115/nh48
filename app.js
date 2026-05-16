async function loadGridProgress() {

    const response =
        await fetch("gridprogress.json");

    const data =
        await response.json();

    let html = "";

    html += `
        <h2>
            Total Completed:
            ${data.totalCompleted}
            / ${data.totalPossible}
        </h2>

        <h3>
            ${data.percent}% Complete
        </h3>
    `;

    html += `
        <table>

            <tr>
                <th>Month</th>
                <th>Completed</th>
                <th>Remaining</th>
                <th>Peaks Remaining</th>
            </tr>
    `;

    data.months.forEach(month => {

        html += `
            <tr>

                <td>
                    ${month.month}
                </td>

                <td>
                    ${month.completed}/48
                </td>

                <td>
                    ${month.remaining}
                </td>

                <td>
                    ${month.peaks.join(", ")}
                </td>

            </tr>
        `;
    });

    html += "</table>";

    document.getElementById(
        "gridProgress"
    ).innerHTML = html;
}

loadGridProgress();