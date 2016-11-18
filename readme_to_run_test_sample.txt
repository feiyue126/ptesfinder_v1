# Download and extract PTESFinder v 1. 

# Recommended: Download copy bundled with bowtie, bowtie2 and bedtools (v 2.15.0), if you do not have these tools installed on your system (and on your path) OR if you encounter any bedtools-specific errors.

# PTESFinder requires Java runtime environment version 1.6 and over

wget http://sourceforge.net/projects/ptesfinder-v1/files/ptesfinder_v1.tar.gz/download -O ptesfinder_v1.tar.gz

wget http://sourceforge.net/projects/ptesfinder-v1/files/data.tar.gz/download -O data.tar.gz

tar -zxvf ptesfinder_v1.tar.gz

# To run test sample, do the following:

# *either have path to genomic reference fasta and pre-built bowtie2 genomic index ready or download the data folder, extract and transfer contents to PTESFinder/data

cd PTESFinder

mkdir test

./PTESFinder.sh -r test_reads.fastq -d test -t data/test_refseq.bed -g path_to_genomic_fasta -b path_to_genomic_bowtie2_index  -s 65 -u 7 -c code/

# to run a guided analysis with list of previously discovered structures, use:

./PTESFinder.sh -r test_reads.fastq -d test -t data/test_refseq.bed -g path_to_genomic_fasta -b path_to_genomic_bowtie2_index  -s 65 -u 7 -c code/ -C data/tempCan.fa -P data/tempConstructs.fa


# run should complete in approximately 1 min

# identified structures can be seen in test/PTESFinder/annotated-ptes.bed

# output descriptor
# =================

# annotated-ptes.bed:
# -----------------
# - chromosome chr1, chr2 ..etc
# - start 
# - stop
# - structure (eg. NM_018449.8.7) -> implies circRNA from UBAP2 locus, involving exons 8 & 7
# - raw_count -> number of circRNA supporting reads
# - strand
# - mean_flanking_can_count -> mean counts for canonical junctions flanking circRNA; e.g. 6-7 & 8-9 junctions of UBAP2
# - sum_flanking_can_count -> sum of reads supporting canonical junctions flanking circRNA
# - max_can_count -> maximum canonical junction count observed in locus
# - mean_can_count -> mean canonical junction count from locus
# - sum_can_count -> total canonical junction counts from locus
# - canonical_expression (eg 1.2_3.0|2.3_5.0|3.4_23.0|4.5_50.0) -> implies 3 reads support canonical junction 1-2, 5 reads support 2-3, 23 reads support 3-4 and 50 reads support canonical junction 4-5.


# Others:
# ------
# PTESReads ->  PTES supporting reads post-filter
# flanking-canonical-counts.tsv.bed -> All flanking canonical junctions in bed format
# ptes-raw.counts -> Putative PTES junctions pre-filter
# canonical-raw.counts -> Canonical junctions pre-filter 
