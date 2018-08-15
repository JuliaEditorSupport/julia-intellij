# TODO
# let a=[:asd :asdas]

let buffer = IOBuffer(),
		col_1_label = "Token",
		col_2_label = "Abbreviation",
		col_1 = mapreduce(s -> length(string(s)), max, __TOKENS__, init =  length(col_1_label) ) + 2,
		col_2 = mapreduce(s -> length(string(s)), max, __SHORTNAMES__, init = length(col_2_label)) + 2
		println(buffer, "| ", rpad(col_1_label, col_1), " | ", rpad(col_2_label, col_2), " |")
		println(buffer, "|:", rpad("-", col_1, "-"),    " | ", rpad("-", col_2, "-"),    ":|")
		for (long, short) in zip(__TOKENS__, __SHORTNAMES__)
				println(buffer, "| ", rpad("`$long`", col_1), " | ", rpad("`$short`", col_2), " |")
		end
		Highlights.takebuf_str(buffer)
end