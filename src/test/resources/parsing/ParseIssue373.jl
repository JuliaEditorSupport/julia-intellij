test_csvs = [f for f in readdir(test_path) if endswith(f, ".csv")]

function get_csv_path(input_path)
    csvs = [f for f in readdir(input_path) if endswith(f, ".csv")]
    return csvs
end