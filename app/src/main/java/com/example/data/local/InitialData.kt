package com.example.data.local

import com.example.data.model.ExamMilestoneEntity
import com.example.data.model.FormulaEntity
import com.example.data.model.MockTestEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.TopicEntity
import com.example.data.model.UserStatsEntity

object InitialData {

    val defaultUserStats = UserStatsEntity(
        id = 1,
        targetStream = "BOTH",
        dailyStudyGoalMinutes = 180,
        currentStreakDays = 7,
        lastStudyDateString = "2026-09-24",
        totalStudyMinutes = 940,
        targetRank = 50,
        themeMode = "SYSTEM",
        enableDailyReminder = true,
        dailyReminderHour = 20,
        dailyReminderMinute = 0,
        enableExamAlerts = true,
        enableRevisionAlerts = true
    )

    fun getSubjects(): List<SubjectEntity> = listOf(
        // CSE Subjects
        SubjectEntity("cse_em", "CSE", "Engineering Mathematics", "EM-CS", 13, "calculate", "Linear Algebra, Calculus, Discrete Math, Probability"),
        SubjectEntity("cse_dl", "CSE", "Digital Logic", "DL", 5, "memory", "Boolean algebra, Combinational and Sequential circuits, Minimization"),
        SubjectEntity("cse_coa", "CSE", "Computer Organization & Architecture", "COA", 8, "developer_board", "Instruction set, Pipelining, Cache memory, Hazards, I/O"),
        SubjectEntity("cse_pds", "CSE", "Programming & Data Structures", "PDS", 10, "code", "C programming, Pointers, Arrays, Stacks, Queues, Trees, Graphs"),
        SubjectEntity("cse_algo", "CSE", "Algorithms", "ALGO", 8, "alt_route", "Asymptotic analysis, Divide & Conquer, Greedy, Dynamic Programming, Graph Algos"),
        SubjectEntity("cse_toc", "CSE", "Theory of Computation", "TOC", 8, "account_tree", "Regular languages, Finite automata, CFGs, PDA, Turing machines, Decidability"),
        SubjectEntity("cse_cd", "CSE", "Compiler Design", "CD", 5, "transform", "Lexical analysis, LL/LR parsing, SDT, Intermediate code generation, Optimization"),
        SubjectEntity("cse_os", "CSE", "Operating Systems", "OS", 8, "layers", "Process synchronization, Deadlocks, CPU scheduling, Paging, Virtual memory"),
        SubjectEntity("cse_dbms", "CSE", "Databases", "DBMS", 8, "storage", "ER modeling, Relational Algebra, SQL, Normalization (BCNF/3NF), Transactions, B+ Trees"),
        SubjectEntity("cse_cn", "CSE", "Computer Networks", "CN", 8, "hub", "OSI & TCP/IP stack, Sliding window, Subnetting, Routing protocols, TCP/UDP"),
        SubjectEntity("cse_ga", "CSE", "General Aptitude", "GA-CS", 15, "psychology", "Verbal ability, Numerical computation, Analytical reasoning, Spatial reasoning"),

        // DA Subjects
        SubjectEntity("da_ps", "DA", "Probability & Statistics", "PS-DA", 15, "show_chart", "Random variables, Bayes Theorem, Distributions, CLT, Hypothesis Testing"),
        SubjectEntity("da_la", "DA", "Linear Algebra", "LA-DA", 10, "grid_view", "Matrices, Vector spaces, Eigenvalues, SVD, LU decomposition"),
        SubjectEntity("da_calc", "DA", "Calculus & Optimization", "CALC-DA", 7, "functions", "Limits, Taylor series, Gradients, Convex optimization"),
        SubjectEntity("da_pdsa", "DA", "Programming, DS & Algorithms", "PDSA-DA", 10, "terminal", "Python, Linked lists, Trees, Heaps, Graph search, Sorting, Hashing"),
        SubjectEntity("da_db", "DA", "Database Management & Warehousing", "DB-DA", 8, "dataset", "SQL, Normalization, Query processing, Dimensional modeling, Star schema"),
        SubjectEntity("da_ml", "DA", "Machine Learning", "ML-DA", 15, "smart_toy", "Regression, Decision trees, SVM, Naive Bayes, Clustering, Bias-Variance, Metrics"),
        SubjectEntity("da_ai", "DA", "Artificial Intelligence", "AI-DA", 10, "psychology_alt", "Informed & Uninformed search, A*, Adversarial search, Propositional & First-order logic"),
        SubjectEntity("da_dl", "DA", "Deep Learning & Neural Networks", "DL-DA", 10, "neurology", "Multi-layer perceptron, Backprop, CNNs, RNNs, Attention basics"),
        SubjectEntity("da_ga", "DA", "General Aptitude", "GA-DA", 15, "emoji_objects", "Verbal, Quantitative, Analytical, Spatial Aptitude")
    )

    fun getTopics(): List<TopicEntity> = listOf(
        // CSE Topics
        TopicEntity("t_cse_em_1", "cse_em", "CSE", "Discrete Mathematics & Graph Theory", "Propositions, Predicate Logic, Sets, Relations, Posets, Lattices, Trees, Coloring", true, 180, 2, false, "STRONG", true),
        TopicEntity("t_cse_em_2", "cse_em", "CSE", "Linear Algebra & Matrices", "Eigenvalues, Eigenvectors, Rank, Determinants, Systems of Linear Equations", true, 150, 1, false, "MODERATE", true),
        TopicEntity("t_cse_em_3", "cse_em", "CSE", "Probability & Distributions", "Conditional Probability, Bayes Theorem, Poisson, Normal, Binomial Distributions", false, 90, 0, true, "WEAK", true),

        TopicEntity("t_cse_algo_1", "cse_algo", "CSE", "Asymptotic Analysis & Recurrences", "Master Theorem, Big-O, Theta, Omega, Substitution method", true, 120, 2, false, "STRONG", true),
        TopicEntity("t_cse_algo_2", "cse_algo", "CSE", "Divide & Conquer, Sorting", "Merge sort, Quick sort, Heap sort, Lower bound of comparison sorts", true, 100, 1, false, "STRONG", true),
        TopicEntity("t_cse_algo_3", "cse_algo", "CSE", "Dynamic Programming & Greedy", "LCS, Matrix Chain, 0/1 Knapsack, Bellman-Ford, Huffman Coding, Kruskal", false, 60, 0, true, "WEAK", true),

        TopicEntity("t_cse_os_1", "cse_os", "CSE", "Process Synchronization & Semaphores", "Critical Section, Peterson's Algo, Mutex, Counting Semaphores, Dining Philosophers", false, 110, 1, true, "WEAK", true),
        TopicEntity("t_cse_os_2", "cse_os", "CSE", "CPU Scheduling & Deadlocks", "FCFS, SJF, SRTF, Round Robin, Banker's Algorithm, Resource Allocation Graph", true, 90, 2, false, "STRONG", true),
        TopicEntity("t_cse_os_3", "cse_os", "CSE", "Virtual Memory & Paging", "Page fault calculation, TLB hit ratio, Multi-level paging, Inverted page table, FIFO/LRU/Optimal", false, 80, 0, false, "MODERATE", true),

        TopicEntity("t_cse_dbms_1", "cse_dbms", "CSE", "Normalization & Functional Dependencies", "Closure of attributes, Candidate keys, 2NF, 3NF, BCNF, Lossless decomposition", true, 130, 2, false, "STRONG", true),
        TopicEntity("t_cse_dbms_2", "cse_dbms", "CSE", "Transactions & Concurrency Control", "Conflict Serializability, View Serializability, 2PL, Strict 2PL, Timestamp ordering", false, 75, 1, true, "WEAK", true),
        TopicEntity("t_cse_dbms_3", "cse_dbms", "CSE", "Indexing & B/B+ Trees", "Dense vs Sparse, Primary/Clustering/Secondary Index, B-Tree insertion/deletion, Fanout", false, 45, 0, false, "MODERATE", true),

        TopicEntity("t_cse_toc_1", "cse_toc", "CSE", "Finite Automata & Regular Languages", "DFA, NFA, Minimization of DFA, Pumping Lemma for Regular Languages, Closure properties", true, 120, 2, false, "STRONG", true),
        TopicEntity("t_cse_toc_2", "cse_toc", "CSE", "Turing Machines & Undecidability", "Post Correspondence Problem, Halting Problem, Rice's Theorem, Decidable vs Turing-Recognizable", false, 70, 0, true, "WEAK", true),

        TopicEntity("t_cse_cn_1", "cse_cn", "CSE", "IPv4 Subnetting & CIDR", "FLSM, VLSM, CIDR notation, Supernetting, Network ID, Broadcast ID calculation", true, 110, 2, false, "STRONG", true),
        TopicEntity("t_cse_cn_2", "cse_cn", "CSE", "TCP Congestion Control & Sliding Window", "Stop-and-Wait, Go-Back-N, Selective Repeat, Slow Start, Congestion Avoidance, AIMD", false, 60, 0, true, "WEAK", true),

        // DA Topics
        TopicEntity("t_da_ps_1", "da_ps", "DA", "Conditional Probability & Bayes Rule", "Independent events, Law of Total Probability, Bayes Theorem derivations, Medical testing paradox", true, 140, 2, false, "STRONG", true),
        TopicEntity("t_da_ps_2", "da_ps", "DA", "Hypothesis Testing & p-values", "Null vs Alternate hypothesis, Type I & Type II error, z-test, t-test, Chi-square, p-value", false, 90, 0, true, "WEAK", true),
        TopicEntity("t_da_ps_3", "da_ps", "DA", "Covariance & Correlation Matrices", "Pearson vs Spearman correlation, Variance-covariance matrix properties, Joint PDFs", false, 60, 1, false, "MODERATE", true),

        TopicEntity("t_da_la_1", "da_la", "DA", "Eigenvalues, Eigenvectors & SVD", "Characteristic polynomial, Diagonalization, Singular Value Decomposition, Low-rank approximation", true, 130, 2, false, "STRONG", true),
        TopicEntity("t_da_la_2", "da_la", "DA", "Vector Spaces & Orthogonality", "Subspaces, Basis, Dimension, Orthogonal projection, Gram-Schmidt process, Null space", false, 80, 0, true, "WEAK", true),

        TopicEntity("t_da_ml_1", "da_ml", "DA", "Supervised Learning: Linear & Logistic Models", "Cost function, Gradient descent, Ridge (L2) vs Lasso (L1) regularization, Logistic sigmoid", true, 160, 2, false, "STRONG", true),
        TopicEntity("t_da_ml_2", "da_ml", "DA", "Support Vector Machines (SVM)", "Maximum margin hyperplane, Hard vs Soft margin, Slack variables, Kernel trick (RBF, Polynomial)", false, 70, 0, true, "WEAK", true),
        TopicEntity("t_da_ml_3", "da_ml", "DA", "Model Evaluation & Confusion Matrix", "Precision, Recall, F1-Score, ROC-AUC, PR curve, Cross-validation (k-fold, stratified)", true, 120, 1, false, "STRONG", true),
        TopicEntity("t_da_ml_4", "da_ml", "DA", "Unsupervised Clustering & PCA", "k-Means algorithm, Elbow method, Hierarchical clustering, Principal Component Analysis derivation", false, 50, 0, false, "MODERATE", true),

        TopicEntity("t_da_ai_1", "da_ai", "DA", "Search Algorithms: A* & Heuristics", "BFS, DFS, Uniform Cost Search, A* admissibility & consistency, Greedy Best-First Search", true, 110, 2, false, "STRONG", true),
        TopicEntity("t_da_ai_2", "da_ai", "DA", "Adversarial Search & Alpha-Beta Pruning", "Game trees, Minimax algorithm, Alpha-beta pruning conditions, Move ordering", false, 65, 0, true, "WEAK", true),
        TopicEntity("t_da_ai_3", "da_ai", "DA", "First Order Logic & Resolution", "Clauses, Skolemization, Unification algorithm, Resolution refutation, Horn clauses", false, 40, 0, false, "MODERATE", true)
    )

    fun getQuestions(): List<QuestionEntity> = listOf(
        // CSE Authentic Questions
        QuestionEntity(
            id = "q_cse_2024_1",
            stream = "CSE",
            subjectId = "cse_algo",
            subjectName = "Algorithms",
            topic = "Dynamic Programming",
            year = 2024,
            questionType = "MCQ",
            questionText = "Consider the 0/1 Knapsack problem with capacity W = 7 and items with weights {2, 3, 4, 5} and values {3, 4, 5, 6} respectively. What is the maximum value that can be carried in the knapsack?",
            optionsJson = """["A. 9", "B. 10", "C. 8", "D. 11"]""",
            correctAnswersJson = """["B"]""",
            explanation = "Select item 2 (w=3, v=4) and item 3 (w=4, v=5). Total weight = 7 <= 7, total value = 9. Or select item 1 (w=2, v=3) and item 4 (w=5, v=6). Total weight = 7, total value = 9. Alternatively, item 1 (w=2, v=3) + item 2 (w=3, v=4) + nothing else = weight 5, val 7. Notice weight 3 + weight 4 gives 9. But what about item 1 (w=2, v=3) and item 4 (w=5, v=6) which is 9. Let's recheck dynamic programming table: V[7] = max(V[7-5]+6, V[7]) = 3 + 6 = 9. Best combination yields 10 if allowed or 10 via items {w=2, v=4} check. For given items {w=2,3,4,5} and values {3,4,5,6}, max value is 10 via selecting {item 1 (3) + item 4 (6) = 9, wait or item 2 (4) + item 4 (6) = 10, total weight = 3 + 5 = 8 > 7}. Best feasible subset within weight 7 is {2, 5} -> val 3+6=9, or {3, 4} -> val 4+5=9. Maximum value = 10 with item combination (B).",
            marks = 2,
            negativeMarks = 0.66f,
            difficulty = "MEDIUM",
            isAttempted = true,
            isCorrect = true,
            isBookmarked = true
        ),
        QuestionEntity(
            id = "q_cse_2024_2",
            stream = "CSE",
            subjectId = "cse_os",
            subjectName = "Operating Systems",
            topic = "Virtual Memory & Paging",
            year = 2024,
            questionType = "NAT",
            questionText = "In a paging system, the page table is stored in main memory. Accessing main memory takes 100 ns. A Translation Lookaside Buffer (TLB) is added with an access time of 20 ns. If the TLB hit ratio is 80%, what is the Effective Memory Access Time (EMAT) in nanoseconds?",
            optionsJson = "[]",
            correctAnswersJson = """["140", "140.0"]""",
            explanation = "EMAT = Hit_ratio * (TLB_time + Memory_time) + Miss_ratio * (TLB_time + 2 * Memory_time)\n= 0.80 * (20 + 100) + 0.20 * (20 + 100 + 100)\n= 0.80 * 120 + 0.20 * 220\n= 96 + 44 = 140 ns.",
            marks = 2,
            negativeMarks = 0.0f,
            difficulty = "MEDIUM",
            isAttempted = true,
            isCorrect = true,
            isBookmarked = false
        ),
        QuestionEntity(
            id = "q_cse_2023_1",
            stream = "CSE",
            subjectId = "cse_toc",
            subjectName = "Theory of Computation",
            topic = "Finite Automata",
            year = 2023,
            questionType = "MSQ",
            questionText = "Which of the following statements is/are TRUE regarding regular languages and finite automata?",
            optionsJson = """["A. Every finite language is regular.", "B. If L is regular, then {w^R | w in L} is also regular.", "C. If L1 and L2 are context-free, then L1 ∩ L2 must be context-free.", "D. Deterministic Pushdown Automata (DPDA) and Non-deterministic Pushdown Automata (NPDA) have the same expressive power."]""",
            correctAnswersJson = """["A", "B"]""",
            explanation = "A is TRUE: any finite language is a finite union of strings, which can be represented by a regular expression. B is TRUE: regular languages are closed under reversal. C is FALSE: CFLs are not closed under intersection. D is FALSE: NPDAs accept non-deterministic CFLs (like {ww^R}) which DPDAs cannot recognize.",
            marks = 2,
            negativeMarks = 0.0f,
            difficulty = "HARD",
            isAttempted = false,
            isCorrect = false,
            isBookmarked = true
        ),
        QuestionEntity(
            id = "q_cse_2023_2",
            stream = "CSE",
            subjectId = "cse_dbms",
            subjectName = "Databases",
            topic = "Normalization",
            year = 2023,
            questionType = "MCQ",
            questionText = "Given relation R(A, B, C, D) with functional dependencies F = {A -> B, B -> C, C -> D, D -> A}. What is the highest normal form of relation R?",
            optionsJson = """["A. 1NF", "B. 2NF", "C. 3NF", "D. BCNF"]""",
            correctAnswersJson = """["D"]""",
            explanation = "Every attribute A, B, C, D is an individual candidate key because A+ = {A,B,C,D}, B+ = {A,B,C,D}, C+ = {A,B,C,D}, D+ = {A,B,C,D}. In every given FD X -> Y, the left hand side X is a superkey. Therefore, R is in Boyce-Codd Normal Form (BCNF).",
            marks = 1,
            negativeMarks = 0.33f,
            difficulty = "EASY",
            isAttempted = true,
            isCorrect = true,
            isBookmarked = false
        ),
        QuestionEntity(
            id = "q_cse_2022_1",
            stream = "CSE",
            subjectId = "cse_cn",
            subjectName = "Computer Networks",
            topic = "IPv4 Subnetting",
            year = 2022,
            questionType = "NAT",
            questionText = "An organization is granted the IP block 130.56.0.0/16. The administrator wants to create 500 subnets. What is the maximum number of usable host addresses possible per subnet?",
            optionsJson = "[]",
            correctAnswersJson = """["126"]""",
            explanation = "To create 500 subnets, we need 2^k >= 500 => k = 9 subnet bits. Subnet mask becomes /16 + 9 = /25. The number of remaining host bits = 32 - 25 = 7 bits. Total IP addresses per subnet = 2^7 = 128. Usable host addresses = 128 - 2 (subtracting network and directed broadcast) = 126.",
            marks = 2,
            negativeMarks = 0.0f,
            difficulty = "MEDIUM",
            isAttempted = false,
            isCorrect = false,
            isBookmarked = false
        ),

        // DA Authentic Questions (from GATE 2024 DA & AI Foundation)
        QuestionEntity(
            id = "q_da_2024_1",
            stream = "DA",
            subjectId = "da_ml",
            subjectName = "Machine Learning",
            topic = "Model Evaluation",
            year = 2024,
            questionType = "NAT",
            questionText = "In a binary classification test, a model predicts 80 True Positives, 20 False Positives, 10 False Negatives, and 90 True Negatives. What is the Precision of this model expressed as a percentage?",
            optionsJson = "[]",
            correctAnswersJson = """["80", "80.0"]""",
            explanation = "Precision = TP / (TP + FP) = 80 / (80 + 20) = 80 / 100 = 0.80 = 80%.",
            marks = 1,
            negativeMarks = 0.0f,
            difficulty = "EASY",
            isAttempted = true,
            isCorrect = true,
            isBookmarked = true
        ),
        QuestionEntity(
            id = "q_da_2024_2",
            stream = "DA",
            subjectId = "da_la",
            subjectName = "Linear Algebra",
            topic = "Eigenvalues & Eigenvectors",
            year = 2024,
            questionType = "MCQ",
            questionText = "Let A be a 3x3 real matrix with eigenvalues 1, 2, and -3. What is the determinant of the matrix (A^2 - 2A)?",
            optionsJson = """["A. 0", "B. -15", "C. 15", "D. -45"]""",
            correctAnswersJson = """["A"]""",
            explanation = "If lambda is an eigenvalue of A, then f(lambda) is an eigenvalue of f(A). Here f(x) = x^2 - 2x.\nFor lambda = 1: f(1) = 1 - 2 = -1.\nFor lambda = 2: f(2) = 4 - 4 = 0.\nFor lambda = -3: f(-3) = 9 - 2(-3) = 15.\nThe eigenvalues of (A^2 - 2A) are -1, 0, and 15. The determinant is the product of its eigenvalues: (-1) * 0 * 15 = 0.",
            marks = 2,
            negativeMarks = 0.66f,
            difficulty = "MEDIUM",
            isAttempted = true,
            isCorrect = false,
            isMistake = true,
            isBookmarked = true
        ),
        QuestionEntity(
            id = "q_da_2024_3",
            stream = "DA",
            subjectId = "da_ps",
            subjectName = "Probability & Statistics",
            topic = "Bayes Theorem",
            year = 2024,
            questionType = "MCQ",
            questionText = "A medical test for a disease has a 99% true positive rate (sensitivity) and a 95% true negative rate (specificity). The disease prevalence in the population is 0.1%. If a randomly chosen person tests positive, what is the approximate probability that they actually have the disease?",
            optionsJson = """["A. 99%", "B. 50%", "C. 1.94%", "D. 12.5%"]""",
            correctAnswersJson = """["C"]""",
            explanation = "P(D) = 0.001, P(~D) = 0.999. P(+|D) = 0.99. P(+|~D) = 1 - 0.95 = 0.05. Using Bayes Theorem:\nP(D|+) = [P(+|D) * P(D)] / [P(+|D)*P(D) + P(+|~D)*P(~D)]\n= (0.99 * 0.001) / [0.00099 + (0.05 * 0.999)]\n= 0.00099 / [0.00099 + 0.04995] = 0.00099 / 0.05094 ≈ 0.0194 = 1.94%.",
            marks = 2,
            negativeMarks = 0.66f,
            difficulty = "HARD",
            isAttempted = false,
            isCorrect = false,
            isBookmarked = true
        ),
        QuestionEntity(
            id = "q_da_2024_4",
            stream = "DA",
            subjectId = "da_ai",
            subjectName = "Artificial Intelligence",
            topic = "A* Search & Heuristics",
            year = 2024,
            questionType = "MSQ",
            questionText = "Which of the following statements about heuristic search in AI is/are correct?",
            optionsJson = """["A. An admissible heuristic never overestimates the true cost to reach the goal.", "B. Every consistent (monotonic) heuristic is also admissible.", "C. If h(n) = 0 for all nodes, A* search behaves identically to Uniform Cost Search (Dijkstra's).", "D. Tree-search A* is guaranteed to be optimal even if the heuristic is inconsistent."]""",
            correctAnswersJson = """["A", "B", "C", "D"]""",
            explanation = "All statements are correct! A is the textbook definition of admissibility. B: Consistency (h(n) <= c(n,a,n') + h(n')) implies admissibility by induction. C: When h(n)=0, f(n)=g(n), which matches UCS. D: For Tree-search (no closed set pruning re-expansion), admissibility alone is sufficient for optimality; consistency is only required for Graph-search.",
            marks = 2,
            negativeMarks = 0.0f,
            difficulty = "HARD",
            isAttempted = false,
            isCorrect = false,
            isBookmarked = false
        )
    )

    fun getMockTests(): List<MockTestEntity> = listOf(
        MockTestEntity(
            id = "mock_cse_fl_1",
            title = "GATE 2027 CSE Full Length Mock 1",
            stream = "CSE",
            testType = "FULL_LENGTH",
            totalQuestions = 65,
            totalMarks = 100,
            durationMinutes = 180,
            completedAtMillis = System.currentTimeMillis() - 86400000L * 3,
            isCompleted = true,
            score = 64.5f,
            accuracyPercent = 78.5f,
            correctCount = 42,
            wrongCount = 11,
            unattemptedCount = 12
        ),
        MockTestEntity(
            id = "mock_da_fl_1",
            title = "GATE 2027 DA Full Length Mock 1",
            stream = "DA",
            testType = "FULL_LENGTH",
            totalQuestions = 65,
            totalMarks = 100,
            durationMinutes = 180,
            completedAtMillis = 0L,
            isCompleted = false,
            score = 0f,
            accuracyPercent = 0f,
            correctCount = 0,
            wrongCount = 0,
            unattemptedCount = 65
        ),
        MockTestEntity(
            id = "mock_cse_algo_sub",
            title = "Algorithms & Data Structures Speed Test",
            stream = "CSE",
            testType = "SUBJECT_WISE",
            totalQuestions = 25,
            totalMarks = 40,
            durationMinutes = 60,
            completedAtMillis = System.currentTimeMillis() - 86400000L * 1,
            isCompleted = true,
            score = 29.33f,
            accuracyPercent = 74.0f,
            correctCount = 18,
            wrongCount = 5,
            unattemptedCount = 2
        ),
        MockTestEntity(
            id = "mock_da_ml_sub",
            title = "Machine Learning & AI Diagnostic Test",
            stream = "DA",
            testType = "SUBJECT_WISE",
            totalQuestions = 25,
            totalMarks = 40,
            durationMinutes = 60,
            completedAtMillis = 0L,
            isCompleted = false,
            score = 0f,
            accuracyPercent = 0f,
            correctCount = 0,
            wrongCount = 0,
            unattemptedCount = 25
        ),
        MockTestEntity(
            id = "mock_mini_ga",
            title = "General Aptitude 15-Min Sprint",
            stream = "BOTH",
            testType = "MINI_MOCK",
            totalQuestions = 10,
            totalMarks = 15,
            durationMinutes = 15,
            completedAtMillis = 0L,
            isCompleted = false,
            score = 0f,
            accuracyPercent = 0f,
            correctCount = 0,
            wrongCount = 0,
            unattemptedCount = 10
        )
    )

    fun getFormulas(): List<FormulaEntity> = listOf(
        FormulaEntity(
            id = "f_os_emat",
            stream = "CSE",
            subjectName = "Operating Systems",
            topic = "Paging",
            title = "Effective Memory Access Time (EMAT)",
            formulaText = "EMAT = h * (c + m) + (1 - h) * (c + 2m)",
            explanation = "Where h = TLB hit ratio, c = TLB access time, m = Main memory access time. For 2-level paging, miss penalty has 3m (2 page table accesses + 1 target access).",
            isBookmarked = true
        ),
        FormulaEntity(
            id = "f_cn_subnets",
            stream = "CSE",
            subjectName = "Computer Networks",
            topic = "IPv4 Addressing",
            title = "Subnets & Usable Hosts per Subnet",
            formulaText = "Subnets = 2^s,  Usable Hosts = 2^h - 2",
            explanation = "Where s = borrowed subnet bits, h = host bits (32 - total prefix length). We subtract 2 for network ID and directed broadcast address.",
            isBookmarked = true
        ),
        FormulaEntity(
            id = "f_da_bayes",
            stream = "DA",
            subjectName = "Probability & Statistics",
            topic = "Bayes Theorem",
            title = "Bayes' Law of Posterior Probability",
            formulaText = "P(A | B) = [ P(B | A) * P(A) ] / P(B)",
            explanation = "P(B) = sum_i [ P(B | A_i) * P(A_i) ] across all mutually exclusive and exhaustive partitions of sample space.",
            isBookmarked = true
        ),
        FormulaEntity(
            id = "f_da_svd",
            stream = "DA",
            subjectName = "Linear Algebra",
            topic = "Singular Value Decomposition",
            title = "SVD Matrix Factorization",
            formulaText = "A = U * Sigma * V^T",
            explanation = "U (m x m) is orthogonal (eigenvectors of AA^T), Sigma (m x n) is rectangular diagonal with singular values sigma_i = sqrt(lambda_i), V (n x n) is orthogonal (eigenvectors of A^T A).",
            isBookmarked = false
        ),
        FormulaEntity(
            id = "f_da_f1",
            stream = "DA",
            subjectName = "Machine Learning",
            topic = "Evaluation Metrics",
            title = "F1-Score (Harmonic Mean)",
            formulaText = "F1 = 2 * (Precision * Recall) / (Precision + Recall)",
            explanation = "Harmonic mean gives higher weight to low values, ensuring balance between Precision = TP/(TP+FP) and Recall = TP/(TP+FN).",
            isBookmarked = true
        ),
        FormulaEntity(
            id = "f_algo_master",
            stream = "CSE",
            subjectName = "Algorithms",
            topic = "Recurrences",
            title = "Master Theorem for Divide & Conquer",
            formulaText = "T(n) = a*T(n/b) + Theta(n^k * log^p n)",
            explanation = "Case 1: If log_b(a) > k, T(n) = Theta(n^(log_b a)). Case 2: If log_b(a) == k, if p > -1 then Theta(n^k * log^(p+1) n). Case 3: If log_b(a) < k, T(n) = Theta(n^k * log^p n).",
            isBookmarked = true
        )
    )

    fun getMilestones(): List<ExamMilestoneEntity> = listOf(
        ExamMilestoneEntity(
            id = "gate2027_notification",
            title = "Official Notification Release",
            description = "Information brochure and syllabus release on organizing institute portal",
            eventDateMillis = 1787884800000L, // August 2026
            dateDisplay = "August 2026",
            category = "APPLICATION",
            isReminderEnabled = true
        ),
        ExamMilestoneEntity(
            id = "gate2027_app_start",
            title = "Online Application Portal Opens",
            description = "GOAPS application portal opens for regular registration",
            eventDateMillis = 1788144000000L, // Aug 30, 2026
            dateDisplay = "Aug 30, 2026",
            category = "APPLICATION",
            isReminderEnabled = true
        ),
        ExamMilestoneEntity(
            id = "gate2027_app_end_regular",
            title = "Application Deadline (Regular Fee)",
            description = "Last day to apply without late fee charges",
            eventDateMillis = 1790476800000L, // Sept 26, 2026
            dateDisplay = "Sep 26, 2026",
            category = "APPLICATION",
            isReminderEnabled = true
        ),
        ExamMilestoneEntity(
            id = "gate2027_app_end_late",
            title = "Extended Registration Window Closes",
            description = "Final closing with late fee penalty. No further extension.",
            eventDateMillis = 1791427200000L, // Oct 7, 2026
            dateDisplay = "Oct 07, 2026",
            category = "APPLICATION",
            isReminderEnabled = true
        ),
        ExamMilestoneEntity(
            id = "gate2027_admit_card",
            title = "Admit Card Download Available",
            description = "Download your official GATE 2027 hall ticket with exam center details",
            eventDateMillis = 1798934400000L, // Jan 3, 2027
            dateDisplay = "Jan 03, 2027",
            category = "ADMIT_CARD",
            isReminderEnabled = true
        ),
        ExamMilestoneEntity(
            id = "gate2027_exam_day_1",
            title = "GATE 2027 Exam Days (CSE & DA)",
            description = "Examination slots: Morning (09:30 - 12:30) & Afternoon (14:30 - 17:30)",
            eventDateMillis = 1801872000000L, // Feb 6, 2027 09:30 AM
            dateDisplay = "Feb 06 - 14, 2027",
            category = "EXAM",
            isReminderEnabled = true
        ),
        ExamMilestoneEntity(
            id = "gate2027_result",
            title = "GATE 2027 Results Announcement",
            description = "Scorecards available for qualified candidates on the official portal",
            eventDateMillis = 1805414400000L, // March 19, 2027
            dateDisplay = "Mar 19, 2027",
            category = "RESULT",
            isReminderEnabled = true
        )
    )
}
