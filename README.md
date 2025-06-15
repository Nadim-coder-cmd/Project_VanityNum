# Project_VanityNum
Java AWS Lambda for Amazon Connect. On incoming call, generates all vanity number permutations from caller's phone number using recursion. Filters top 5 based on vowel count and dictionary word matches, stores in DynamoDB, and returns the top 3 most memorable suggestions. 

# Base requirements
- Java 21
- Apaache maven (to integrate integrate/enable aws services dependencies)

# Solution implementation reasons/key challenges

I chose AWS Lambda for its serverless nature and ability to integrate seamlessly with Amazon Connect, reducing infrastructure overhead and optimizing resource utilization. The core logic uses recursion to generate all valid vanity permutations from the caller's number. This approach allows full coverage of possible combinations and is easy to reason about.

To improve relevance and memorability of suggestions, I implemented a heuristic ranking system based on vowel count—since vowels improve pronounceability—and later extended it to check for valid English words using a dictionary. DynamoDB was selected for persistence due to its scalability and low-latency reads/writes.

One key challenge I faced was the exponential growth in the number of permutations during recursive vanity number generation — especially for longer phone numbers. At the prototype stage, I kept the implementation simple and generated all combinations first, followed by sorting and selecting the top 5 based on heuristics like vowel count and word matching.
While this works for shorter numbers, I recognize it doesn't scale efficiently. To overccome this I added a final variable MAX_SIZE to limit the total resutling permetutations, and short-circuit unneccessary computations.

# Shortcuts taken to proving as bad practise in production
- In-memory dictionary: The dictionary is loaded into memory once, which is efficient for small word sets, but could lead to cold start latency or memory issues with larger files in real-world Lambda deployments. A cache layer or external word service might be more scalable.
- No input validation or sanitization: Assumes a valid numeric string is always received, which is risky in production. Input should be validated and potentially normalized before processing.

# With more time the following could have been implemented
- Enhancing scoring heuristics: Beyond vowels and dictionary matches, I would explore NLP techniques to rank names based on syllable flow, repetition, or brand-like characteristics.
- Asynchronous processing: For very large search spaces, I’d consider breaking generation and scoring into separate steps or using AWS Step Functions for long-running workflows.
- API Gateway and IAM policy hardening: I'd define a proper REST interface, restrict permissions to least privilege, and enforce security best practices across resources.
- Unit tests and CI/CD pipeline: I’d set up automated testing using JUnit and a GitHub Actions or CodePipeline workflow to ensure safety with each change
- I’d improve error handling around DynamoDB writes and dictionary loading to gracefully fail and notify downstream systems if needed.
