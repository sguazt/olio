<?php
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
    
/**
 * Class for accessing the local file system.
 * Allows creation, retrieval and storage of files, querying
 * existence of a file, etc.
 * This class is similar to LocalFS except that does not create any new file.
 */
class FakeLocalFS extends LocalFS {
	
    function create($srcpath, $replication_factor='1',	$overwrite = 'true')
	{
		return true;
	}

	function getFullPath($filename, $attrs = null)
	{
		return parent::getFullPath($this->makeFakeFileName($fileName));
	}

	protected function makeFakeFileName($fileName)
	{
		$fileName = strtolower($fileName);
		if (preg_match('/(.*?)([0-9]*)([0-9]{3})(.*)/', $fileName, $matches))
		{
			if ($matches[2] == '')
			{
				$matches[2] = 0;
			}
			if ($matches[1] == 'p')
			{
				$matches[2] = $matches[2]% 10; // for workload scale 102, 10(000) persons
			}
			elseif ($matches[1] == 'e')
			{
				$matches[2] = $matches[2]% 7; // and 7(000) events are available
			}
			if ($matches[2] == 0)
			{
				$matches[2] = '';
			}
			$matches[0] = '';
			$matches[3] = preg_replace('/^[0]+/','',$matches[3]);
			if ($matches[2] == '' && $matches[3] == '')
			{
				$matches[3] = '1000';
			}
			$fileName = implode($matches);

			return $fileName;
		}
	}
}
?>
