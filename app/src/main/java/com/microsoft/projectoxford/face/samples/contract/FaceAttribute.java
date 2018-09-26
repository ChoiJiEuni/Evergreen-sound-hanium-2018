//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Face-Android
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.microsoft.projectoxford.face.samples.contract;

import com.microsoft.projectoxford.face.contract.Accessory;
import com.microsoft.projectoxford.face.samples.contract.Blur;
import com.microsoft.projectoxford.face.samples.contract.Emotion;
import com.microsoft.projectoxford.face.contract.Exposure;
import com.microsoft.projectoxford.face.contract.FacialHair;
import com.microsoft.projectoxford.face.samples.contract.Glasses;
import com.microsoft.projectoxford.face.contract.Hair;
import com.microsoft.projectoxford.face.samples.contract.HeadPose;
import com.microsoft.projectoxford.face.contract.Makeup;
import com.microsoft.projectoxford.face.samples.contract.Noise;
import com.microsoft.projectoxford.face.contract.Occlusion;

public class FaceAttribute {
    public double age;

    public String gender;

    public double smile;

    public FacialHair facialHair;

    public HeadPose headPose;

    public Glasses glasses;

    public Emotion emotion;

    public Blur blur;

    public Exposure exposure;

    public Noise noise;

    public Makeup makeup;

    public Accessory[] accessories;

    public Occlusion occlusion;

    public Hair hair;
}
