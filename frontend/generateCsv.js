// Import the `fs` module with ES module syntax
import { createWriteStream } from 'fs';

// Constants
const OUTPUT_FILE = 'employees.csv'; // Generated CSV file
const TOTAL_ROWS = 20_000; // Change this number for larger files

// Utility to generate a random date between two dates
function randomDate(start, end) {
  const date = new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()));
  return date.toISOString().split('T')[0]; // Format as 'YYYY-MM-DD'
}

// Function to generate and write a large CSV
async function generateLargeCSV() {
  console.time('CSV Generation'); // Track time

  const fileStream = createWriteStream(OUTPUT_FILE);

  // Write the header
  fileStream.write('EmpID,ProjectID,DateFrom,DateTo\n');

  // Stream data into the file
  for (let i = 1; i <= TOTAL_ROWS; i++) {
    const empID = i; // Incremental EmpID
    const projectID = Math.floor(Math.random() * 500) + 100; // Random ProjectID (e.g., 100-599)
    const dateFrom = randomDate(new Date(2020, 0, 1), new Date(2021, 0, 1)); // Random Date From (2020)
    const dateTo = randomDate(new Date(2021, 0, 2), new Date(2022, 0, 1)); // Random Date To (2021)

    // Write a line
    fileStream.write(`${empID},${projectID},${dateFrom},${dateTo}\n`);

    // Log progress every 100,000 rows
    if (i % 100_000 === 0) {
      console.log(`Generated ${i} rows`);
    }
  }

  fileStream.end(); // Close the write stream

  console.timeEnd('CSV Generation'); // Log completion time
  console.log(`CSV generation completed. Saved to ${OUTPUT_FILE}`);
}

// Run the CSV generation
generateLargeCSV().catch((err) => console.error(err));
