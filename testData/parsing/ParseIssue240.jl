a = rand(5, 5)
b = a[:, end:-1:1]
a = rand(5, 5, 5)
b = a[:, end:-1:1, :]
