# Reflection: Manual UI Testing vs AI-Assisted UI Testing

## Ease of Writing and Running Tests

Writing Playwright tests manually in Java required significant effort in understanding the website's DOM structure and identifying the correct selectors for each element. I used Playwright's codegen tool to record my interactions with the DePaul bookstore website, which generated Java code with the appropriate selectors. Even with codegen, I had to organize the generated code into proper JUnit test methods, add assertions, set up the test lifecycle with `@BeforeAll` and `@AfterAll`, and handle edge cases like popups and page load timing. The entire process of writing, debugging, and refining the manual tests took several hours, especially when selectors broke between runs or when the website's prices changed.

In contrast, AI-assisted testing through the Playwright MCP agent was considerably faster. By describing the test workflow in natural language — for example, "search for earbuds, filter by brand JBL, add to cart, and verify the cart shows 1 item" — the AI was able to generate a complete, runnable test file in seconds. The generated code included proper JUnit structure, assertions, and Playwright API calls without needing to manually look up method signatures or selector patterns. What took hours manually was accomplished in minutes with AI assistance.

## Accuracy and Reliability of Generated Tests

The manually written tests, after debugging, were highly accurate because each selector was captured directly from the live website using codegen. I was able to verify each step visually as I recorded the interactions, which gave me confidence that the selectors matched the actual page elements. However, the selectors were sometimes fragile — elements like the Price filter had different selector patterns between sessions (sometimes `svg`, sometimes `path`), which caused intermittent failures.

The AI-generated tests produced syntactically correct and logically structured code, but they relied on me providing accurate context about the website's current state, such as the correct prices and element names. The AI could not independently verify whether a selector would work on the live site. In practice, this meant the AI-generated tests needed the same validation and debugging process as the manual ones. The AI was excellent at structuring the test code but could not guarantee runtime accuracy without human oversight.

## Maintenance Effort

Maintaining manually written tests requires a developer to re-record interactions with codegen or manually inspect the DOM whenever the website changes. During this assignment, the product price changed from $149.98 to $164.98, which caused all price-related assertions to fail across multiple test cases. Identifying and updating every affected assertion was tedious and error-prone.

With AI-assisted testing, maintenance becomes easier because you can simply re-describe the expected behavior in natural language and have the AI regenerate the affected tests. For example, updating the price in a prompt and regenerating the test file is faster than manually searching through code for every hardcoded value. However, the AI still depends on the developer knowing what changed and providing the correct updated values.

## Limitations and Issues Encountered

The main limitation of manual testing was the time investment in debugging selectors. Playwright's codegen tool was invaluable, but the generated selectors were not always stable across different browser sessions. Elements like filter buttons, checkout buttons (where `.nth(1)` was needed), and dynamically loaded content required additional manual tuning that codegen could not anticipate.

For AI-assisted testing, the primary limitation was the lack of real-time browser interaction. The AI could not see or interact with the live website, so it had to rely on descriptions and previously captured selectors. Additionally, setting up the Playwright MCP server required Node.js, which added an extra dependency. The AI also tended to generate more concise code that sometimes skipped important wait times between page transitions, which needed to be added manually to prevent timing-related failures.

## Conclusion

Both approaches have clear strengths. Manual testing with codegen gives you precise control and visual verification, making it ideal for complex or fragile UI workflows. AI-assisted testing dramatically reduces the time needed to scaffold test code and is especially useful for generating boilerplate structure, organizing test cases, and quickly iterating on changes. The most effective approach is a combination of both: using AI to generate the initial test structure and then refining selectors and timing through manual codegen recording and debugging. As AI tools like Playwright MCP continue to improve, the gap between the two approaches will likely narrow, but human oversight remains essential for ensuring test reliability on live websites.