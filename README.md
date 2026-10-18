# Assignment 4 Facade Pattern study reference

Prepared for Nurdaulet Mukhitden, SE-2537. Option B: Online Order Processing.
AI-generated reference for learning. See AI_ASSISTANCE.md.
GitHub repository URL: not published. Nothing was uploaded to LMS.

## Build and run

```sh
javac --release 17 -encoding UTF-8 -d out "@sources.txt"
java -cp out Main
java -cp out FacadeChecks
```

Tested with Amazon Corretto 17.0.13; checks finish with `SUMMARY: 10/10 PASS`.
All state lives in memory. No real payment, shipment or email is sent.
Default catalog: notebook 1200.00, initial stock 10; pen 300.00, initial stock 20.
The demonstration orders 2 notebooks and 3 pens for a total of 3300.00.

| Role | Class | Responsibility |
| --- | --- | --- |
| Client | Main | Use facade and domain values only |
| Facade | OrderFacade | Coordinate services and compensation |
| Subsystem | InventoryService | Validate, reserve, price and release stock |
| Subsystem | PaymentProcessor | Simulate charge and refund |
| Subsystem | ShippingService | Simulate scheduling and cancellation |
| Subsystem | NotificationService | Record confirmation messages |
| Domain | Cart, Customer, OrderResult | Immutable API values |

placeOrder reserves, charges, schedules and records a confirmation. A known service
failure cancels any shipment, refunds any charge and releases any reservation.
cancelOrder is idempotent. The public subsystem classes can also be used directly
by advanced clients; they contain no facade references.

report.pdf includes an English explanation, UML, five annotated Clean Code excerpts
and verification. uml.puml is editable source. demo-output.txt is actual check output.
application-output.txt is actual Main output. DEFENSE.md contains study prompts.

## Remaining course requirements

Deadline: 18 October 2026 at 23:59. Defense: last practice of Week 7.
A GitHub URL and mandatory personal defense are not supplied by this local folder.
The course restricts AI to examples and clarification, so do not present this
reference as independently authored code or report. No multi-day history is claimed.
