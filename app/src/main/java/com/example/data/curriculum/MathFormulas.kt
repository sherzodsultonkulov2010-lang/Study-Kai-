package com.example.data.curriculum

data class MathFormulaItem(
    val id: String,
    val branch: String,
    val name: String,
    val formula: String,
    val description: String,
    val example: String
)

object MathFormulas {
    val branches = listOf(
        "All",
        "Arithmetic",
        "Algebra",
        "Geometry",
        "Trigonometry",
        "Calculus",
        "Probability & Stats",
        "Vectors & Matrices"
    )

    val allFormulas: List<MathFormulaItem> = listOf(
        // Arithmetic
        MathFormulaItem("ar1", "Arithmetic", "Commutative Law of Addition", "a + b = b + a", "Order of addends does not change the sum.", "7 + 12 = 12 + 7 = 19"),
        MathFormulaItem("ar2", "Arithmetic", "Distributive Property", "a(b + c) = ab + ac", "Multiplying a sum by a factor equals multiplying each term separately.", "3(4 + 5) = 12 + 15 = 27"),
        MathFormulaItem("ar3", "Arithmetic", "Percentage Formula", "Part = (Percent / 100) × Whole", "Finds a percentage portion of a base quantity.", "25% of 80 = 0.25 × 80 = 20"),
        MathFormulaItem("ar4", "Arithmetic", "Percent Change", "% Change = ((New - Old) / Old) × 100", "Measures relative increase or decrease.", "From 40 to 50: +25%"),
        MathFormulaItem("ar5", "Arithmetic", "Simple Interest", "I = P × r × t", "Interest earned on principal P at annual rate r over t years.", "I = 1000 × 0.05 × 2 = 100"),
        MathFormulaItem("ar6", "Arithmetic", "Compound Interest", "A = P(1 + r/n)^(nt)", "Accumulated amount with compounding frequency n.", "P=1000, r=0.06, n=1, t=2 -> 1123.60"),
        MathFormulaItem("ar7", "Arithmetic", "Fraction Addition", "a/b + c/d = (ad + bc) / bd", "Combines two fractions using a common denominator.", "1/3 + 1/4 = 7/12"),
        MathFormulaItem("ar8", "Arithmetic", "Proportion Rule", "a/b = c/d  <=>  a·d = b·c", "Cross-multiplication checks equality of two ratios.", "3/4 = 9/12 since 36 = 36"),
        MathFormulaItem("ar9", "Arithmetic", "Greatest Common Divisor & LCM", "GCD(a,b) × LCM(a,b) = a × b", "Relates highest common factor and least common multiple.", "GCD(12,18)=6, LCM(12,18)=36"),
        MathFormulaItem("ar10", "Arithmetic", "Average Speed", "v_avg = Total Distance / Total Time", "Overall rate of travel across an entire trip.", "150 km in 2.5 h -> 60 km/h"),

        // Algebra
        MathFormulaItem("al1", "Algebra", "Difference of Squares", "a² - b² = (a - b)(a + b)", "Factors the difference between two squared expressions.", "x² - 49 = (x - 7)(x + 7)"),
        MathFormulaItem("al2", "Algebra", "Square of a Sum", "(a + b)² = a² + 2ab + b²", "Binomial expansion for a sum squared.", "(x + 3)² = x² + 6x + 9"),
        MathFormulaItem("al3", "Algebra", "Square of a Difference", "(a - b)² = a² - 2ab + b²", "Binomial expansion for a difference squared.", "(x - 5)² = x² - 10x + 25"),
        MathFormulaItem("al4", "Algebra", "Sum & Difference of Cubes", "a³ ± b³ = (a ± b)(a² ∓ ab + b²)", "Factors cubic binomials.", "x³ - 8 = (x - 2)(x² + 2x + 4)"),
        MathFormulaItem("al5", "Algebra", "Quadratic Formula", "x = (-b ± √(b² - 4ac)) / (2a)", "Finds roots of ax² + bx + c = 0.", "x² - 5x + 6 = 0 -> x = 2, 3"),
        MathFormulaItem("al6", "Algebra", "Discriminant", "Δ = b² - 4ac", "Determines number of real roots: Δ>0 (2), Δ=0 (1), Δ<0 (0).", "For x²+2x+5: Δ = -16 (no real roots)"),
        MathFormulaItem("al7", "Algebra", "Vieta's Formulas", "x₁ + x₂ = -b/a,  x₁·x₂ = c/a", "Relates sum and product of quadratic roots to coefficients.", "x² - 7x + 10 = 0 -> sum 7, prod 10"),
        MathFormulaItem("al8", "Algebra", "Slope of a Line", "m = (y₂ - y₁) / (x₂ - x₁)", "Rate of change between two points in the Cartesian plane.", "(1,2) to (4,8) -> m = 2"),
        MathFormulaItem("al9", "Algebra", "Slope-Intercept Form", "y = mx + b", "Linear equation with slope m and y-intercept b.", "y = 3x - 4"),
        MathFormulaItem("al10", "Algebra", "Vertex of Parabola", "x_v = -b / (2a),  y_v = f(x_v)", "Coordinates of the minimum or maximum of ax² + bx + c.", "y = x² - 6x + 5 -> (3, -4)"),
        MathFormulaItem("al11", "Algebra", "Exponent Product & Power Rules", "a^m · a^n = a^(m+n),  (a^m)^n = a^(mn)", "Simplifies powers with matching bases.", "2³ · 2⁴ = 2⁷ = 128"),
        MathFormulaItem("al12", "Algebra", "Logarithm Laws", "log_b(xy) = log_b(x) + log_b(y),  log_b(x^k) = k·log_b(x)", "Converts products/powers inside logs into sums/products.", "log₂(8·4) = 3 + 2 = 5"),
        MathFormulaItem("al13", "Algebra", "Change of Base Formula", "log_b(x) = ln(x) / ln(b)", "Evaluates logarithms in any base using natural or base-10 log.", "log₃(81) = ln(81)/ln(3) = 4"),
        MathFormulaItem("al14", "Algebra", "Arithmetic Sequence nth Term", "a_n = a₁ + (n - 1)d", "Finds the nth term given first term a₁ and common difference d.", "3, 7, 11... -> a₅ = 3 + 4(4) = 19"),
        MathFormulaItem("al15", "Algebra", "Arithmetic Series Sum", "S_n = n(a₁ + a_n) / 2", "Sum of first n terms of an arithmetic progression.", "1+2+...+100 = 100(101)/2 = 5050"),
        MathFormulaItem("al16", "Algebra", "Geometric Sequence & Sum", "a_n = a₁·r^(n-1),  S_n = a₁(1 - r^n)/(1 - r)", "Progression with constant ratio r ≠ 1.", "2, 6, 18 -> a₄ = 2·3³ = 54"),

        // Geometry
        MathFormulaItem("ge1", "Geometry", "Pythagorean Theorem", "a² + b² = c²", "Relates legs a, b and hypotenuse c of a right triangle.", "3² + 4² = 5² (9 + 16 = 25)"),
        MathFormulaItem("ge2", "Geometry", "Triangle Area", "A = ½ · b · h", "Half the product of base b and perpendicular height h.", "b=10, h=6 -> A = 30"),
        MathFormulaItem("ge3", "Geometry", "Heron's Formula", "A = √(s(s-a)(s-b)(s-c)),  s = (a+b+c)/2", "Triangle area from side lengths a, b, c.", "Sides 5,6,7 -> s=9, A=6√6"),
        MathFormulaItem("ge4", "Geometry", "Equilateral Triangle Area", "A = (s²√3) / 4", "Area of a triangle with three equal sides s.", "s = 4 -> A = 4√3"),
        MathFormulaItem("ge5", "Geometry", "Interior Angle Sum of n-gon", "Sum = (n - 2) × 180°", "Total interior angle measure of an n-sided polygon.", "Hexagon (n=6): 4 × 180° = 720°"),
        MathFormulaItem("ge6", "Geometry", "Circle Circumference & Area", "C = 2πr,  A = πr²", "Perimeter and area of a circle of radius r.", "r = 5 -> C = 10π, A = 25π"),
        MathFormulaItem("ge7", "Geometry", "Arc Length & Sector Area", "L = rθ,  A = ½r²θ (θ in radians)", "Measures curved arc length and slice area of a circle.", "r=6, θ=π/3 -> L=2π, A=6π"),
        MathFormulaItem("ge8", "Geometry", "Distance Formula", "d = √((x₂ - x₁)² + (y₂ - y₁)²)", "Euclidean distance between two points.", "(0,0) to (6,8) -> d = 10"),
        MathFormulaItem("ge9", "Geometry", "Midpoint Formula", "M = ((x₁ + x₂)/2, (y₁ + y₂)/2)", "Center point of a segment joining two coordinates.", "(2,4) & (8,10) -> (5,7)"),
        MathFormulaItem("ge10", "Geometry", "Circle Equation", "(x - h)² + (y - k)² = r²", "Standard equation with center (h, k) and radius r.", "(x - 2)² + (y + 1)² = 16"),
        MathFormulaItem("ge11", "Geometry", "Cylinder Volume & Surface Area", "V = πr²h,  SA = 2πr(r + h)", "3D circular prism capacity and outer surface area.", "r=3, h=5 -> V = 45π"),
        MathFormulaItem("ge12", "Geometry", "Cone Volume", "V = ⅓πr²h", "One-third the volume of a cylinder with matching base and height.", "r=3, h=4 -> V = 12π"),
        MathFormulaItem("ge13", "Geometry", "Sphere Volume & Surface Area", "V = (4/3)πr³,  SA = 4πr²", "Volume and surface area of a ball of radius r.", "r=3 -> V=36π, SA=36π"),

        // Trigonometry
        MathFormulaItem("tr1", "Trigonometry", "SOH-CAH-TOA Definitions", "sinθ = Opp/Hyp,  cosθ = Adj/Hyp,  tanθ = Opp/Adj", "Right-triangle ratios for acute angle θ.", "3-4-5 triangle: sinθ = 3/5"),
        MathFormulaItem("tr2", "Trigonometry", "Pythagorean Identity", "sin²θ + cos²θ = 1", "Fundamental trigonometric identity for all real angles.", "sin²30° + cos²30° = 0.25 + 0.75 = 1"),
        MathFormulaItem("tr3", "Trigonometry", "Quotient & Reciprocal Identities", "tanθ = sinθ/cosθ,  secθ = 1/cosθ,  cscθ = 1/sinθ", "Expresses secondary trig functions using sine and cosine.", "tan45° = sin45°/cos45° = 1"),
        MathFormulaItem("tr4", "Trigonometry", "Law of Sines", "a / sinA = b / sinB = c / sinC = 2R", "Relates sides and opposite angles in any triangle.", "Used in AAS and ASA triangles"),
        MathFormulaItem("tr5", "Trigonometry", "Law of Cosines", "c² = a² + b² - 2ab·cosC", "Generalizes Pythagorean theorem to non-right triangles.", "C=60°, a=5, b=8 -> c=7"),
        MathFormulaItem("tr6", "Trigonometry", "Double-Angle Formulas", "sin(2θ) = 2sinθcosθ,  cos(2θ) = cos²θ - sin²θ", "Computes trig values of 2θ from θ.", "sin(60°) = 2sin(30°)cos(30°)"),
        MathFormulaItem("tr7", "Trigonometry", "Angle Addition Identities", "sin(A±B) = sinAcosB ± cosAsinB", "Expands sine of sums and differences.", "sin(75°) = sin(45°+30°)"),
        MathFormulaItem("tr8", "Trigonometry", "Degrees to Radians", "radians = degrees × (π / 180)", "Converts angular measure between degrees and radians.", "180° = π rad, 90° = π/2 rad"),

        // Calculus
        MathFormulaItem("ca1", "Calculus", "Limit Definition of Derivative", "f'(x) = lim(h→0) [f(x+h) - f(x)] / h", "Instantaneous rate of change / tangent line slope.", "For x²: f'(x) = 2x"),
        MathFormulaItem("ca2", "Calculus", "Power Rule (Derivative)", "d/dx [x^n] = n·x^(n-1)", "Differentiates polynomial and power functions.", "d/dx(x⁵) = 5x⁴"),
        MathFormulaItem("ca3", "Calculus", "Product Rule", "(fg)' = f'g + fg'", "Derivative of a product of two differentiable functions.", "d/dx(x·sinx) = sinx + x·cosx"),
        MathFormulaItem("ca4", "Calculus", "Quotient Rule", "(f/g)' = (f'g - fg') / g²", "Derivative of a ratio of functions where g ≠ 0.", "Differentiates rational expressions"),
        MathFormulaItem("ca5", "Calculus", "Chain Rule", "d/dx [f(g(x))] = f'(g(x)) · g'(x)", "Derivative of composite functions.", "d/dx(sin(3x)) = 3cos(3x)"),
        MathFormulaItem("ca6", "Calculus", "Exponential & Log Derivatives", "d/dx(e^x) = e^x,  d/dx(ln x) = 1/x", "Key transcendental derivatives.", "d/dx(e^(2x)) = 2e^(2x)"),
        MathFormulaItem("ca7", "Calculus", "Trig Derivatives", "d/dx(sin x) = cos x,  d/dx(cos x) = -sin x", "Rates of change for sine and cosine.", "d/dx(tan x) = sec²x"),
        MathFormulaItem("ca8", "Calculus", "Power Rule (Integration)", "∫ x^n dx = x^(n+1)/(n+1) + C  (n ≠ -1)", "Antiderivative of power functions.", "∫ x² dx = x³/3 + C"),
        MathFormulaItem("ca9", "Calculus", "Fundamental Theorem of Calculus", "∫[a..b] f(x) dx = F(b) - F(a)", "Connects definite integrals with antiderivatives F'(x)=f(x).", "∫[0..2] 3x² dx = 2³ - 0 = 8"),
        MathFormulaItem("ca10", "Calculus", "Integration by Parts", "∫ u dv = u·v - ∫ v du", "Integration counterpart to the product rule.", "Used for ∫ x·e^x dx"),

        // Probability & Statistics
        MathFormulaItem("ps1", "Probability & Stats", "Arithmetic Mean", "μ = (Σ x_i) / N", "Average value of a numeric dataset.", "Mean(4, 8, 12) = 8"),
        MathFormulaItem("ps2", "Probability & Stats", "Variance & Standard Deviation", "σ² = Σ(x_i - μ)² / N,  σ = √(σ²)", "Measures spread of values around the mean.", "Larger σ means greater dispersion"),
        MathFormulaItem("ps3", "Probability & Stats", "Permutations", "P(n, k) = n! / (n - k)!", "Ordered arrangements of k items chosen from n.", "P(5, 2) = 5×4 = 20"),
        MathFormulaItem("ps4", "Probability & Stats", "Combinations", "C(n, k) = n! / (k!(n - k)!)", "Unordered subsets of k items chosen from n.", "C(5, 2) = 10"),
        MathFormulaItem("ps5", "Probability & Stats", "Addition Rule of Probability", "P(A ∪ B) = P(A) + P(B) - P(A ∩ B)", "Probability that at least one of two events occurs.", "Avoids double-counting overlap"),
        MathFormulaItem("ps6", "Probability & Stats", "Conditional Probability & Bayes", "P(A|B) = P(A ∩ B) / P(B)", "Probability of A given that event B has occurred.", "P(A|B) = P(B|A)P(A)/P(B)"),
        MathFormulaItem("ps7", "Probability & Stats", "Binomial Probability", "P(X = k) = C(n, k) p^k (1-p)^(n-k)", "Probability of k successes in n independent trials.", "Coin tosses, pass/fail trials"),

        // Vectors & Matrices
        MathFormulaItem("vm1", "Vectors & Matrices", "Vector Magnitude", "|v| = √(v_x² + v_y² + v_z²)", "Euclidean length of a vector.", "v=(3, 4) -> |v| = 5"),
        MathFormulaItem("vm2", "Vectors & Matrices", "Dot Product", "u · v = u_x v_x + u_y v_y = |u||v|cosθ", "Scalar product used to test orthogonality (u·v = 0).", "(1,2)·(3,4) = 11"),
        MathFormulaItem("vm3", "Vectors & Matrices", "2×2 Matrix Determinant", "det([a b; c d]) = ad - bc", "Measures area scaling and invertibility (det ≠ 0).", "det([3 2; 1 4]) = 10"),
        MathFormulaItem("vm4", "Vectors & Matrices", "2×2 Matrix Inverse", "A⁻¹ = (1 / (ad - bc)) [d -b; -c a]", "Multiplicative inverse when ad - bc ≠ 0.", "Satisfies A·A⁻¹ = I")
    )
}
