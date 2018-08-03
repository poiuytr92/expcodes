/***************************************************************************************
* AUTHOR : EXP
* DATE   : 2017-10-6
* MODULE : VS2010_WDK_Demo.C
* 
* Command: 
*	Source of IOCTRL Sample Driver
*
* Description:
*		Demonstrates communications between USER and KERNEL.
*
****************************************************************************************
* Copyright (C) 2017 EXP.
****************************************************************************************/

//#######################################################################################
//# I N C L U D E S
//#######################################################################################

#ifndef CXX_VS2010_WDK_DEMO_H
#include "VS2010_WDK_Demo.h"
#endif

//#include "struct.h"

//////////////////////////////////////////////////////////////////////////

//#######################################################################################
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//@@@@@@@@				D R I V E R   E N T R Y   P O I N T						 @@@@@@@@
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//#######################################################################################

NTSTATUS
DriverEntry(IN PDRIVER_OBJECT pDriverObj, IN PUNICODE_STRING pRegistryString)
{
	NTSTATUS		status = STATUS_SUCCESS;
	UNICODE_STRING  ustrLinkName;
	UNICODE_STRING  ustrDevName;  
	PDEVICE_OBJECT  pDevObj;
	int i = 0;

	DbgPrint("Hello, Driver By VS2010\r\n");

	// 添加 INT 3 系统中断
	#ifdef _AMD64_
		int_3();	// x64系统只能调用 *.asm 汇编文件中声明的函数
	#else
		__asm { int 3 }	// x86系统可以直接使用内联汇编
	#endif
	
	DbgPrint("Goodbye, Driver By VS2010\r\n");


    dprintf("EasySys Sample Driver\r\n"
            "Compiled %s %s\r\nIn DriverEntry : %wZ\r\n",
			__DATE__, __TIME__, pRegistryString);

	// Register dispatch routines
/*
	for(i = 0; i < IRP_MJ_MAXIMUM_FUNCTION; i++)
	{
		pDriverObj->MajorFunction[i] = DispatchCommon;  
	}
*/
	pDriverObj->MajorFunction[IRP_MJ_CREATE] = DispatchCreate;
	pDriverObj->MajorFunction[IRP_MJ_CLOSE] = DispatchClose;

	// Dispatch routine for communications
	pDriverObj->MajorFunction[IRP_MJ_DEVICE_CONTROL] = DispatchDeviceControl;

	// Unload routine
	pDriverObj->DriverUnload = DriverUnload;

	// Initialize the device name.
	RtlInitUnicodeString(&ustrDevName, NT_DEVICE_NAME);

	// Create the device object and device extension
	status = IoCreateDevice(pDriverObj, 
				0,
				&ustrDevName, 
				FILE_DEVICE_UNKNOWN,
				0,
				FALSE,
				&pDevObj);

	if(!NT_SUCCESS(status))
	{
		dprintf("Error, IoCreateDevice = 0x%x\r\n", status);
		return status;
	}

    //// Get a pointer to our device extension
    //deviceExtension = (PDEVICE_EXTENSION) deviceObject->DeviceExtension;

    //// Save a pointer to the device object
    //deviceExtension->DeviceObject = deviceObject;

	if(IoIsWdmVersionAvailable(1,0x10))
	{
		//如果是支持符号链接用户相关性的系统
		RtlInitUnicodeString(&ustrLinkName, SYMBOLIC_LINK_GLOBAL_NAME);
	}
	else
	{
		//不支持
		RtlInitUnicodeString(&ustrLinkName, SYMBOLIC_LINK_NAME);
	}
	
	// Create a symbolic link to allow USER applications to access it. 
	status = IoCreateSymbolicLink(&ustrLinkName, &ustrDevName);  
	
	if(!NT_SUCCESS(status))
	{
		dprintf("Error, IoCreateSymbolicLink = 0x%x\r\n", status);
		
		IoDeleteDevice(pDevObj); 
		return status;
	}	

	//
	//	TODO: Add initialization code here.
	//

    //// Tell the I/O Manger to do BUFFERED IO
    //deviceObject->Flags |= DO_BUFFERED_IO;

    //// Save the DeviveObject
    //deviceExtension->DeviceObject = deviceObject;

	dprintf("DriverEntry Success\r\n");

	return STATUS_SUCCESS;
}

VOID
DriverUnload(IN PDRIVER_OBJECT pDriverObj)
{	
	UNICODE_STRING strLink;

	// Unloading - no resources to free so just return.
	dprintf("Unloading...\r\n");;	

	//
	// TODO: Add uninstall code here.
	//
	
	// Delete the symbolic link
	RtlInitUnicodeString(&strLink, SYMBOLIC_LINK_NAME);
	IoDeleteSymbolicLink(&strLink);

	// Delete the DeviceObject
	IoDeleteDevice(pDriverObj->DeviceObject);

	dprintf("Unloaded Success\r\n");

	return;
}

NTSTATUS
DispatchCreate(IN PDEVICE_OBJECT pDevObj, IN PIRP pIrp)
{
	pIrp->IoStatus.Status = STATUS_SUCCESS;
	pIrp->IoStatus.Information = 0;

	IoCompleteRequest(pIrp, IO_NO_INCREMENT);

	return STATUS_SUCCESS;
}


NTSTATUS
DispatchClose(IN PDEVICE_OBJECT pDevObj, IN PIRP pIrp)
{
	pIrp->IoStatus.Status = STATUS_SUCCESS;
	pIrp->IoStatus.Information = 0;

	IoCompleteRequest(pIrp, IO_NO_INCREMENT);

	// Return success
	return STATUS_SUCCESS;
}

NTSTATUS
DispatchCommon(IN PDEVICE_OBJECT pDevObj, IN PIRP pIrp)
{
	pIrp->IoStatus.Status = STATUS_SUCCESS;
	pIrp->IoStatus.Information = 0L;

	IoCompleteRequest( pIrp, 0 );

	// Return success
	return STATUS_SUCCESS;
}

NTSTATUS 
DispatchDeviceControl(IN PDEVICE_OBJECT pDevObj, IN PIRP pIrp)
{
	NTSTATUS status               = STATUS_INVALID_DEVICE_REQUEST;	 // STATUS_UNSUCCESSFUL
	PIO_STACK_LOCATION pIrpStack  = IoGetCurrentIrpStackLocation(pIrp);
	ULONG uIoControlCode          = 0;
	PVOID pIoBuffer				  = NULL;
	ULONG uInSize                 = 0;
	ULONG uOutSize                = 0;

	// Get the IoCtrl Code
	uIoControlCode = pIrpStack->Parameters.DeviceIoControl.IoControlCode;

	pIoBuffer = pIrp->AssociatedIrp.SystemBuffer;
	uInSize = pIrpStack->Parameters.DeviceIoControl.InputBufferLength;
	uOutSize = pIrpStack->Parameters.DeviceIoControl.OutputBufferLength;

	switch(uIoControlCode)
	{
		case IOCTL_HELLO_WORLD:
			{			
				dprintf("MY_CTL_CODE(0)=%d\r\n,MY_CTL_CODE");

				// Return success
				status = STATUS_SUCCESS;
			}
			break;

		case IOCTRL_REC_FROM_APP:
			{
				// Receive data form Application
				//dprintf("IOCTRL_REC_FROM_APP\r\n");

				// Do we have any data?
				if( uInSize > 0 )
				{
					dprintf("Get Data from App: %ws\r\n", pIoBuffer);
				}

				// Return success
				status = STATUS_SUCCESS;
			}
			break;

		case IOCTRL_SEND_TO_APP:
			{
				// Send data to Application
				//dprintf("IOCTRL_SEND_TO_APP\r\n");
			
				// If we have enough room copy the data upto the App - note copy the terminating character as well...
				if( uOutSize >= strlen( DATA_TO_APP ) + 1 )
				{
					RtlCopyMemory(  pIoBuffer,
									DATA_TO_APP, 
									strlen( DATA_TO_APP ) + 1 );

					// Update the length for the App
					pIrp->IoStatus.Information = strlen( DATA_TO_APP ) + 1;

					dprintf("Send Data to App: %s\r\n", pIoBuffer);
					
					// Return success
					status = STATUS_SUCCESS;
				}
			}
			break;
			
		//
		// TODO: Add execute code here.
		//

		default:
			{
				// Invalid code sent
				dprintf("Unknown IOCTL: 0x%X (%04X,%04X)\r\n", 
                                          uIoControlCode,
                                          DEVICE_TYPE_FROM_CTL_CODE(uIoControlCode),
                                          IoGetFunctionCodeFromCtlCode(uIoControlCode));
				status = STATUS_INVALID_PARAMETER;	
			}
			break;
	}

	if(status == STATUS_SUCCESS)
	{
		pIrp->IoStatus.Information = uOutSize;
	}
	else
	{
		pIrp->IoStatus.Information = 0;
	}

	// Complete the I/O Request
	pIrp->IoStatus.Status = status;

	IoCompleteRequest(pIrp, IO_NO_INCREMENT);

	return status;
}


//
// TODO: Add your module definitions here.
//



/* EOF */
