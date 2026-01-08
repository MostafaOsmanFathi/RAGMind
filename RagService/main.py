import sys

from dotenv import load_dotenv
load_dotenv(verbose=False)
from RagSystem.workers_listener import start_listener


def main():
    concurrent_raq_workers = 2
    concurrent_doc_workers = 5
    try:
        if len(sys.argv) > 3:
            raise ValueError('incorrect number of arguments')
        elif len(sys.argv) == 3:
            concurrent_raq_workers = int(sys.argv[1])
            concurrent_doc_workers = int(sys.argv[2])
        elif len(sys.argv) == 2:
            concurrent_raq_workers = int(sys.argv[1])

        start_listener(concurrent_raq_workers,concurrent_doc_workers)
    except ValueError as e:
            print("invalid args, args is  <number_of_raq_workers> <number_of_doc_workers>")
            exit(1)
    except Exception as e:
        print("Error occured ", e)
        exit(1)


if __name__ == '__main__':
    main()
