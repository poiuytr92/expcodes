#!/bin/bash

export lib_x64=dt_otp_x64.so
export mf_x64=makefile_x64
export lib_x86=dt_otp_x86.so
export mf_x86=makefile_x86

echo "======================================="
echo "make ${lib_x64} start: "
make --file=${mf_x64} all 
make --file=${mf_x64} clean 
echo "make ${lib_x64} end. "
file ${lib_x64}
echo "======================================="
echo "."
echo "======================================="
echo "make ${lib_x86} start: "
make --file=${mf_x86} all 
make --file=${mf_x86} clean 
echo "make ${lib_x86} end. "
file ${lib_x86}
echo "======================================="
