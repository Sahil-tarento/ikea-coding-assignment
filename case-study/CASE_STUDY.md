# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**

*   **Challenges:**
    *   **Shared Resource Attribution**: How do we fairly allocate costs for shared resources, such as a single truck delivery serving multiple stores or a warehouse storing products for multiple business lines? We need a clear attribution model (e.g., activity-based costing).
    *   **Granularity vs. Performance**: Tracking costs at the individual SKU or order level provides high accuracy but generates massive data volumes. Is the system capable of handling this throughput without latency?
    *   **Variable vs. Fixed Costs**: Distinguishing between fixed costs (rent, depreciation) and variable costs (labor, packaging) is critical for accurate unit economics.
    *   **Data Consistency**: Synchronization between physical events (item scanned) and financial events (cost accrued) must be precise to avoid "inventory shrinkage" appearing as a cost variance.

*   **Critical Questions:**
    *   What is the desired level of granularity for reporting (e.g., per Store, per Product Category, or per Order)?
    *   Are there existing standard costs or transfer pricing agreements between Warehouses and Stores?
    *    How do we capture "hidden" costs like reverse logistics (returns) and damaged goods?

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**

*   **Strategies:**
    *   **Inventory Balancing**: optimizing stock placement to minimize "split shipments" (fulfilling one order from multiple warehouses) and inter-warehouse transfers.
    *   **Dynamic Route Optimization**: Using AI/ML to plan delivery routes that minimize fuel consumption and driver time.
    *   **Demand Forecasting**: Better prediction of demand peaks to optimize labor scheduling (preventing overstaffing/overtime) and safety stock levels (reducing holding costs).
    *   **Warehouse Automation**: Identifying high-volume picking zones for potential automation (conveyors, AS/RS).

*   **Identification & Prioritization (ROI Framework):**
    *   **Data-Driven Discovery**: Analyze current cost drivers using Pareto analysis (80/20 rule) to find the biggest bleeders.
    *   **Impact vs. Effort**: Prioritize "Quick Wins" (high impact, low effort) such as optimizing packing materials, followed by strategic projects like network redesign.
    *   **Pilot Testing**: Implement changes in one "Champion" warehouse or region before rolling out globally to validate savings without risking the entire network.

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**

*   **Importance & Benefits:**
    *   **Single Source of Truth**: Eliminates discrepancies between "Operational view" and "Financial view" of inventory and costs.
    *   **Automated Reconciliation**: Drastically reduces the manual effort and error rate during month-end closes.
    *   **Real-time Decision Making**: Managers can see the financial impact of operational decisions (e.g., expediting a shipment) immediately, rather than weeks later.

*   **Integration Strategy:**
    *   **Event-Driven Architecture**: Use an event bus (e.g., Kafka or RabbitMQ) to publish operational events (Received, Shipped, Scrapped) which the financial system consumes to trigger ledger entries. This ensures decoupling and scalability.
    *   **Idempotency**: Ensure that financial transactions are processed exactly once, even in the event of network retries.
    *   **Data Mapping & Validation**: Strict contracts (schemas) for master data (SKUs, Cost Centers, Store IDs) to prevent synchronization failures.
    *   **Traceability**: Every financial entry should trace back to the specific operational event ID for auditability.

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**

*   **Importance:**
    *   **Resource Allocation**: ensuring sufficient cash flow, warehouse capacity, and labor force for peak seasons (e.g., Black Friday).
    *   **Variance Analysis**: measuring actual performance against the plan to detect inefficiencies early (e.g., "Why is packaging cost 20% higher than forecast?").

*   **System Design Considerations:**
    *   **Seasonality Handling**: The model must account for historical seasonal trends and holidays.
    *   **Driver-Based Forecasting**: Instead of just extrapolating dollar amounts, forecast the *drivers* (e.g., "Expected Orders", "Items per Order", "Fuel Price") and calculate the resulting cost. This allows for "What-If" scenario analysis.
    *   **Feedback Loops**: The system should automatically compare Forecast vs. Actuals to refine its prediction models over time (Machine Learning application).
    *   **Flexibility**: Ability to support Rolling Forecasts (re-forecasting every month/quarter) rather than just a static annual budget.

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**

*   **Cost Control Aspects:**
    *   **Parallel Running Costs**: During the transition, there might be a period where both facilities incur costs (rent, security). This "double rent" period must be minimized.
    *   **Decommissioning Costs**: Budgeting for the cleanup, lease termination fees, and asset disposal of the old site.
    *   **Ramp-up Efficiency**: The new warehouse may have lower initial efficiency (learning curve). Tracking this "inefficiency gap" is vital to ensure it doesn't become the new normal.

*   **Preserving Cost History (Why it matters):**
    *   **Trend Analysis (YoY)**: We need to compare "This Year" vs "Last Year" performance for the *Business Unit*, regardless of the physical building change. Creating a completely new code would break this historical continuity.
    *   **Asset Depreciation**: Equipment moved from the old to the new warehouse retains its depreciation schedule.
    *   **Audit Compliance**: Historical records of inventory valuation and write-offs must be retained for tax and audit purposes for 7+ years.
    *   **Baseline for Improvement**: The historical cost per unit of the old warehouse serves as the *benchmark* for the new one. If the new warehouse is more expensive per unit after the stabilization period, the ROI of the replacement is negative.

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
