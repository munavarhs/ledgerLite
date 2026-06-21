import { useEffect, useRef, useState } from "react";

interface LedgerEntry {
  id: string;
  date: string;
  description: string;
  amount: number;
  balance: number;
}

function App() {
  const [entries, setEntries] = useState<LedgerEntry[]>([]);
  const [error, setError] = useState<string>("");

  // Track which cell is "active" in the grid: [rowIndex, colIndex]
  const [activeCell, setActiveCell] = useState<[number, number]>([0, 0]);
  // Ref to the table so we can move focus to the right cell after state changes
  const tableRef = useRef<HTMLTableElement>(null);

  useEffect(() => {
    async function loadLedger() {
      try {
        const loginRes = await fetch("http://localhost:8080/auth/login", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ username: "admin", password: "password123" }),
        });
        const { token } = await loginRes.json();

        const ledgerRes = await fetch("http://localhost:8080/ledger/entries", {
          headers: { Authorization: `Bearer ${token}` },
        });
        const data = await ledgerRes.json();
        setEntries(data.entries);
      } catch {
        setError("Failed to load ledger");
      }
    }
    loadLedger();
  }, []);

  const formatCurrency = (value: number) =>
    new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(value);

  const COLS = 4; // Date, Description, Amount, Balance

  // After activeCell changes, move real DOM focus to that cell
  useEffect(() => {
    if (!tableRef.current || entries.length === 0) return;
    const [row, col] = activeCell;
    const selector = `[data-row="${row}"][data-col="${col}"]`;
    const cell = tableRef.current.querySelector<HTMLElement>(selector);
    cell?.focus();
  }, [activeCell, entries]);

  // Arrow-key handler: update activeCell, clamped to grid bounds
  const handleKeyDown = (e: React.KeyboardEvent) => {
    const [row, col] = activeCell;
    let nextRow = row;
    let nextCol = col;

    switch (e.key) {
      case "ArrowDown":  nextRow = Math.min(row + 1, entries.length - 1); break;
      case "ArrowUp":    nextRow = Math.max(row - 1, 0); break;
      case "ArrowRight": nextCol = Math.min(col + 1, COLS - 1); break;
      case "ArrowLeft":  nextCol = Math.max(col - 1, 0); break;
      default: return; // ignore other keys
    }
    e.preventDefault(); // stop the page from scrolling on arrow keys
    setActiveCell([nextRow, nextCol]);
  };

  if (error) return <p role="alert">{error}</p>;

  return (
    <main style={{ fontFamily: "sans-serif", padding: "2rem", maxWidth: "800px", margin: "0 auto"}}>
      <h1>Payments Ledger</h1>
      <p id="grid-help">Click a cell, then use arrow keys to navigate.</p>

      <table ref={tableRef} aria-describedby="grid-help">
        <caption>Account transaction history</caption>
        <thead>
          <tr>
            <th scope="col">Date</th>
            <th scope="col">Description</th>
            <th scope="col">Amount</th>
            <th scope="col">Balance</th>
          </tr>
        </thead>
        <tbody onKeyDown={handleKeyDown}>
          {entries.map((entry, rowIndex) => {
            const cells = [
              entry.date,
              entry.description,
              formatCurrency(entry.amount),
              formatCurrency(entry.balance),
            ];
            return (
              <tr key={entry.id}>
                {cells.map((value, colIndex) => {
                  const isActive =
                    activeCell[0] === rowIndex && activeCell[1] === colIndex;
                  const Tag = colIndex === 0 ? "th" : "td";
                  return (
                    <Tag
                      key={colIndex}
                      scope={colIndex === 0 ? "row" : undefined}
                      data-row={rowIndex}
                      data-col={colIndex}
                      tabIndex={isActive ? 0 : -1}   // ROVING tabindex
                      onClick={() => setActiveCell([rowIndex, colIndex])}
                      style={{
                        padding: "0.5rem 0.75rem",
                        outline: isActive ? "2px solid #2563eb" : "none",
                        cursor: "pointer",
                      }}
                    >
                      {value}
                    </Tag>
                  );
                })}
              </tr>
            );
          })}
        </tbody>
      </table>
    </main>
  );
}

export default App;